import re
from pathlib import Path

import numpy as np
import pandas as pd
import matplotlib.pyplot as plt
from matplotlib.ticker import ScalarFormatter
from matplotlib.lines import Line2D

# =========================================================
# CONFIGURATION
# =========================================================
INPUT_TXT = "q2_results.txt"
OUTPUT_DIR = Path("q2_paper_figures")
OUTPUT_DIR.mkdir(exist_ok=True)

PATTERN_ORDER = ["Random", "Sorted Ascending", "Sorted Descending", "Partially Sorted"]
STRUCTURE_ORDER = ["Treap", "AVLTreeMap", "TreeMap"]
OPERATION_ORDER = [
    "Batch Insert",
    "Single Insert",
    "Search (hit)",
    "Search (miss)",
    "Deletion",
    "Traversal",
]

FOCUS_OPERATIONS = ["Batch Insert", "Search (hit)", "Deletion", "Traversal"]

STYLE_MAP = {
    "Treap": {
        "marker": "o",
        "linewidth_raw": 1.2,
        "linewidth_trend": 2.2,
        "markersize": 4.6,
        "color": "#ef3b2c",
    },
    "AVLTreeMap": {
        "marker": "s",
        "linewidth_raw": 1.2,
        "linewidth_trend": 2.2,
        "markersize": 4.6,
        "color": "#2170b5",
    },
    "TreeMap": {
        "marker": "^",
        "linewidth_raw": 1.2,
        "linewidth_trend": 2.2,
        "markersize": 4.8,
        "color": "#669aba",
    },
}

plt.rcParams.update({
    "figure.dpi": 160,
    "savefig.dpi": 400,
    "font.size": 11,
    "axes.titlesize": 13,
    "axes.labelsize": 11,
    "legend.fontsize": 10,
    "xtick.labelsize": 10,
    "ytick.labelsize": 10,
    "axes.linewidth": 0.8,
    "axes.facecolor": "white",
    "figure.facecolor": "white",
    "grid.alpha": 0.20,
    "grid.linewidth": 0.5,
})

# =========================================================
# DATA PARSER
# =========================================================
def parse_q2_output(text: str) -> pd.DataFrame:
    """
    Parse the raw benchmark output into a tidy DataFrame.
    """
    lines = text.splitlines()

    current_pattern = None
    current_n = None
    rows = []

    pattern_re = re.compile(r"Input Pattern:\s*(.+)")
    row_with_n_re = re.compile(
        r"^\s*(\d+)\s*\|\s*(.+?)\s*\|\s*([0-9.]+)\s*\|\s*([0-9.]+)\s*\|\s*([0-9.]+)\s*$"
    )
    row_without_n_pipe_re = re.compile(
        r"^\s*\|\s*(.+?)\s*\|\s*([0-9.]+)\s*\|\s*([0-9.]+)\s*\|\s*([0-9.]+)\s*$"
    )
    row_without_n_space_re = re.compile(
        r"^\s{2,}(.+?)\s*\|\s*([0-9.]+)\s*\|\s*([0-9.]+)\s*\|\s*([0-9.]+)\s*$"
    )

    for raw_line in lines:
        line = raw_line.rstrip()
        if not line.strip():
            continue

        m_pat = pattern_re.search(line)
        if m_pat:
            current_pattern = m_pat.group(1).strip()
            current_n = None
            continue

        stripped = line.strip()
        if (
                ("Operation" in line and "Treap" in line)
                or set(stripped) == {"-"}
                or set(stripped) == {"="}
                or stripped.startswith("Q2:")
                or stripped.startswith("Treap vs")
                or stripped.startswith("Each measurement")
        ):
            continue

        m1 = row_with_n_re.match(line)
        if m1:
            current_n = int(m1.group(1))
            operation = m1.group(2).strip()
            treap = float(m1.group(3))
            avl = float(m1.group(4))
            treemap = float(m1.group(5))

            rows.extend([
                {"pattern": current_pattern, "n": current_n, "operation": operation, "structure": "Treap", "time_us": treap},
                {"pattern": current_pattern, "n": current_n, "operation": operation, "structure": "AVLTreeMap", "time_us": avl},
                {"pattern": current_pattern, "n": current_n, "operation": operation, "structure": "TreeMap", "time_us": treemap},
            ])
            continue

        m2 = row_without_n_pipe_re.match(line)
        if m2 and current_n is not None:
            operation = m2.group(1).strip()
            treap = float(m2.group(2))
            avl = float(m2.group(3))
            treemap = float(m2.group(4))

            rows.extend([
                {"pattern": current_pattern, "n": current_n, "operation": operation, "structure": "Treap", "time_us": treap},
                {"pattern": current_pattern, "n": current_n, "operation": operation, "structure": "AVLTreeMap", "time_us": avl},
                {"pattern": current_pattern, "n": current_n, "operation": operation, "structure": "TreeMap", "time_us": treemap},
            ])
            continue

        m3 = row_without_n_space_re.match(line)
        if m3 and current_n is not None:
            operation = m3.group(1).strip()
            treap = float(m3.group(2))
            avl = float(m3.group(3))
            treemap = float(m3.group(4))

            rows.extend([
                {"pattern": current_pattern, "n": current_n, "operation": operation, "structure": "Treap", "time_us": treap},
                {"pattern": current_pattern, "n": current_n, "operation": operation, "structure": "AVLTreeMap", "time_us": avl},
                {"pattern": current_pattern, "n": current_n, "operation": operation, "structure": "TreeMap", "time_us": treemap},
            ])
            continue

    df = pd.DataFrame(rows)
    if df.empty:
        raise ValueError("No data parsed. Please check the format of q2_results.txt.")

    df["pattern"] = pd.Categorical(df["pattern"], categories=PATTERN_ORDER, ordered=True)
    df["structure"] = pd.Categorical(df["structure"], categories=STRUCTURE_ORDER, ordered=True)
    df["operation"] = pd.Categorical(df["operation"], categories=OPERATION_ORDER, ordered=True)
    df = df.sort_values(["operation", "pattern", "structure", "n"]).reset_index(drop=True)

    return df

# =========================================================
# TITLE AND AXIS HELPERS
# =========================================================
def add_bold_underlined_suptitle(fig, title, y=0.985, fontsize=15):
    """
    Add a bold figure title and draw an underline beneath it.
    """
    fig.suptitle(title, fontsize=fontsize, fontweight="bold", y=y)

    line = Line2D(
        [0.22, 0.78],
        [y - 0.02, y - 0.02],
        transform=fig.transFigure,
        color="black",
        linewidth=1.0,
    )
    fig.add_artist(line)


def paper_axes(ax, xlabel="Input size n", ylabel="Time (μs)", use_log_x=True):
    """
    Apply a clean academic plotting style to an axis.
    """
    ax.set_xlabel(xlabel)
    ax.set_ylabel(ylabel)
    ax.grid(True, which="major", axis="both")
    ax.set_axisbelow(True)

    ax.spines["top"].set_visible(False)
    ax.spines["right"].set_visible(False)

    if use_log_x:
        ax.set_xscale("log")
        ax.set_xticks([100, 200, 500, 1000, 2000, 5000, 10000])
        ax.get_xaxis().set_major_formatter(ScalarFormatter())
        ax.ticklabel_format(style="plain", axis="x")
    else:
        xfmt = ScalarFormatter(useMathText=False)
        xfmt.set_scientific(False)
        ax.xaxis.set_major_formatter(xfmt)

# =========================================================
# TREND LINE HELPERS
# =========================================================
def linear_trend_line(x, y, fit_on_log_x=True, clip_nonnegative=True):
    """
    Compute a straight linear trend line.

    If fit_on_log_x is True, the line is fitted on log10(x),
    which makes the straight line visually more consistent on a log-scaled x-axis.

    If clip_nonnegative is True, negative fitted values are clipped to zero,
    since execution time cannot be negative.
    """
    x = np.asarray(x, dtype=float)
    y = np.asarray(y, dtype=float)

    if len(x) < 2:
        trend = y.copy()
    else:
        if fit_on_log_x:
            coeffs = np.polyfit(np.log10(x), y, 1)
            trend = np.polyval(coeffs, np.log10(x))
        else:
            coeffs = np.polyfit(x, y, 1)
            trend = np.polyval(coeffs, x)

    if clip_nonnegative:
        trend = np.maximum(trend, 0.0)

    return trend


def add_series(ax, df_sub):
    """
    Plot each data structure with:
    - dashed line + markers for raw measurements
    - solid straight line for the fitted trend
    """
    for structure in STRUCTURE_ORDER:
        sdata = df_sub[df_sub["structure"] == structure].sort_values("n")
        if sdata.empty:
            continue

        style = STYLE_MAP[structure]
        x = sdata["n"].to_numpy()
        y = sdata["time_us"].to_numpy()

        ax.plot(
            x,
            y,
            label=structure,
            marker=style["marker"],
            linestyle="--",
            linewidth=style["linewidth_raw"],
            markersize=style["markersize"],
            color=style["color"],
            alpha=0.95,
        )

        y_trend = linear_trend_line(x, y, fit_on_log_x=True, clip_nonnegative=True)
        ax.plot(
            x,
            y_trend,
            linestyle="-",
            linewidth=style["linewidth_trend"],
            color=style["color"],
            alpha=0.95,
        )

# =========================================================
# LEGEND AND SAVE HELPERS
# =========================================================
def add_vertical_legend(fig, axes):
    """
    Add a vertical legend at the top-right corner of the figure.
    """
    handles, labels = axes[0].get_legend_handles_labels()
    fig.legend(
        handles,
        labels,
        loc="upper right",
        bbox_to_anchor=(0.995, 0.985),
        ncol=1,
        frameon=False,
        borderaxespad=0.0,
        labelspacing=0.6,
        handlelength=2.2,
    )


def savefig_clean(fig, path: Path):
    """
    Save the figure as a high-resolution image and close it.
    """
    fig.savefig(path, bbox_inches="tight", facecolor="white")
    plt.close(fig)
    print(f"Saved: {path}")


def sanitize_filename(name: str) -> str:
    """
    Convert a plot title into a safe filename.
    """
    return (
        name.lower()
        .replace(" ", "_")
        .replace("(", "")
        .replace(")", "")
        .replace("/", "_")
        .replace("__", "_")
    )

# =========================================================
# FIGURE GENERATORS
# =========================================================
def plot_focus_operation(df: pd.DataFrame, operation: str):
    """
    Create one 2x2 figure for a key operation.
    Each subplot corresponds to one input pattern.
    """
    sub = df[df["operation"] == operation].copy()
    if sub.empty:
        return

    fig, axes = plt.subplots(2, 2, figsize=(12, 8), sharex=False)
    axes = axes.flatten()

    for ax, pattern in zip(axes, PATTERN_ORDER):
        psub = sub[sub["pattern"] == pattern].copy()
        if psub.empty:
            ax.set_visible(False)
            continue

        add_series(ax, psub)
        ax.set_title(pattern, fontweight="bold")
        paper_axes(ax, use_log_x=True)

    add_vertical_legend(fig, axes)
    add_bold_underlined_suptitle(
        fig,
        f"{operation}: Performance Across Input Patterns",
        y=0.98,
        fontsize=15
    )

    plt.tight_layout(rect=[0, 0, 0.92, 0.94])
    out = OUTPUT_DIR / f"focus_{sanitize_filename(operation)}.png"
    savefig_clean(fig, out)


def plot_single_insert_log(df: pd.DataFrame):
    """
    Create a dedicated 2x2 figure for Single Insert.
    The y-axis is logarithmic because the values are very small.
    """
    sub = df[df["operation"] == "Single Insert"].copy()
    if sub.empty:
        return

    fig, axes = plt.subplots(2, 2, figsize=(12, 8), sharex=False)
    axes = axes.flatten()

    for ax, pattern in zip(axes, PATTERN_ORDER):
        psub = sub[sub["pattern"] == pattern].copy()
        if psub.empty:
            ax.set_visible(False)
            continue

        add_series(ax, psub)
        ax.set_title(pattern, fontweight="bold")
        paper_axes(ax, ylabel="Time (μs, log scale)", use_log_x=True)
        ax.set_yscale("log")

    add_vertical_legend(fig, axes)
    add_bold_underlined_suptitle(
        fig,
        "Single Insert: Fine-Grained Comparison (Log Scale)",
        y=0.98,
        fontsize=15
    )

    plt.tight_layout(rect=[0, 0, 0.92, 0.94])
    out = OUTPUT_DIR / "single_insert_log.png"
    savefig_clean(fig, out)


def plot_search_miss(df: pd.DataFrame):
    """
    Create a supplementary 2x2 figure for Search (miss).
    """
    sub = df[df["operation"] == "Search (miss)"].copy()
    if sub.empty:
        return

    fig, axes = plt.subplots(2, 2, figsize=(12, 8), sharex=False)
    axes = axes.flatten()

    for ax, pattern in zip(axes, PATTERN_ORDER):
        psub = sub[sub["pattern"] == pattern].copy()
        if psub.empty:
            ax.set_visible(False)
            continue

        add_series(ax, psub)
        ax.set_title(pattern, fontweight="bold")
        paper_axes(ax, use_log_x=True)

    add_vertical_legend(fig, axes)
    add_bold_underlined_suptitle(
        fig,
        "Search (miss): Supplementary Comparison",
        y=0.98,
        fontsize=15
    )

    plt.tight_layout(rect=[0, 0, 0.92, 0.94])
    out = OUTPUT_DIR / "search_miss_supplementary.png"
    savefig_clean(fig, out)


def plot_summary_average(df: pd.DataFrame):
    """
    Create a 2x2 summary figure for the key operations.
    Each line is averaged across all input patterns.
    """
    sub = df[df["operation"].isin(FOCUS_OPERATIONS)].copy()

    grouped = (
        sub.groupby(["operation", "structure", "n"], observed=False)["time_us"]
        .mean()
        .reset_index()
    )

    fig, axes = plt.subplots(2, 2, figsize=(12, 8), sharex=False)
    axes = axes.flatten()

    for ax, operation in zip(axes, FOCUS_OPERATIONS):
        osub = grouped[grouped["operation"] == operation].copy()
        if osub.empty:
            ax.set_visible(False)
            continue

        add_series(ax, osub)
        ax.set_title(operation, fontweight="bold")
        paper_axes(ax, ylabel="Average time over patterns (μs)", use_log_x=True)

    add_vertical_legend(fig, axes)
    add_bold_underlined_suptitle(
        fig,
        "Summary of Key Operations (Averaged Across Input Patterns)",
        y=0.98,
        fontsize=15
    )

    plt.tight_layout(rect=[0, 0, 0.92, 0.94])
    out = OUTPUT_DIR / "summary_key_operations_average.png"
    savefig_clean(fig, out)


def plot_overview_compact(df: pd.DataFrame):
    """
    Create a compact vertical overview figure for the main operations.
    """
    sub = df[df["operation"].isin(FOCUS_OPERATIONS)].copy()

    fig, axes = plt.subplots(4, 1, figsize=(10, 14), sharex=False)
    if len(FOCUS_OPERATIONS) == 1:
        axes = [axes]

    for ax, operation in zip(axes, FOCUS_OPERATIONS):
        osub = (
            sub[sub["operation"] == operation]
            .groupby(["structure", "n"], observed=False)["time_us"]
            .mean()
            .reset_index()
        )

        add_series(ax, osub)
        ax.set_title(operation, loc="left", fontweight="bold")
        paper_axes(ax, ylabel="Avg. time (μs)", use_log_x=True)

    add_vertical_legend(fig, axes)
    add_bold_underlined_suptitle(
        fig,
        "Compact Overview of Main Performance Trends",
        y=0.995,
        fontsize=15
    )

    plt.tight_layout(rect=[0, 0, 0.90, 0.97])
    out = OUTPUT_DIR / "compact_overview_main_operations.png"
    savefig_clean(fig, out)

# =========================================================
# MAIN
# =========================================================
def main():
    text = Path(INPUT_TXT).read_text(encoding="utf-8")
    df = parse_q2_output(text)

    csv_path = OUTPUT_DIR / "q2_performance_parsed.csv"
    df.to_csv(csv_path, index=False)
    print(f"Saved: {csv_path}")

    for op in FOCUS_OPERATIONS:
        plot_focus_operation(df, op)

    plot_single_insert_log(df)
    plot_search_miss(df)
    plot_summary_average(df)
    plot_overview_compact(df)

    print("\nAll figures generated successfully.")


if __name__ == "__main__":
    main()