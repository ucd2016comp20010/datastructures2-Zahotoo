import re
from pathlib import Path

import pandas as pd
import matplotlib.pyplot as plt
from matplotlib.ticker import ScalarFormatter

# =========================================================
# CONFIG
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
        "linestyle": "-",
        "linewidth": 1.8,
        "markersize": 4.8,
        "color": "#be1420",
    },
    "AVLTreeMap": {
        "marker": "s",
        "linestyle": "--",
        "linewidth": 1.8,
        "markersize": 4.8,
        "color": "#012f48",
    },
    "TreeMap": {
        "marker": "^",
        "linestyle": "-.",
        "linewidth": 1.8,
        "markersize": 5.0,
        "color": "#669aba",
    },
}

# 全局风格：简洁学术风
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
    "grid.alpha": 0.22,
    "grid.linewidth": 0.5,
    "lines.linewidth": 1.8,
    "lines.markersize": 5,
})


# =========================================================
# PARSER
# =========================================================
def parse_q2_output(text: str) -> pd.DataFrame:
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
        raise ValueError("No data parsed. Please check q2_results.txt format.")

    df["pattern"] = pd.Categorical(df["pattern"], categories=PATTERN_ORDER, ordered=True)
    df["structure"] = pd.Categorical(df["structure"], categories=STRUCTURE_ORDER, ordered=True)
    df["operation"] = pd.Categorical(df["operation"], categories=OPERATION_ORDER, ordered=True)
    df = df.sort_values(["operation", "pattern", "structure", "n"]).reset_index(drop=True)

    return df


# =========================================================
# STYLING HELPERS
# =========================================================
def paper_axes(ax, xlabel="Input size n", ylabel="Time (μs)"):
    ax.set_xlabel(xlabel)
    ax.set_ylabel(ylabel)
    ax.grid(True, which="major", axis="both")
    ax.set_axisbelow(True)

    ax.spines["top"].set_visible(False)
    ax.spines["right"].set_visible(False)

    xfmt = ScalarFormatter(useMathText=False)
    xfmt.set_scientific(False)
    ax.xaxis.set_major_formatter(xfmt)


def add_series(ax, df_sub):
    for structure in STRUCTURE_ORDER:
        sdata = df_sub[df_sub["structure"] == structure].sort_values("n")
        if sdata.empty:
            continue

        style = STYLE_MAP[structure]

        ax.plot(
            sdata["n"],
            sdata["time_us"],
            label=structure,
            marker=style["marker"],
            linestyle=style["linestyle"],
            linewidth=style["linewidth"],
            markersize=style["markersize"],
            color=style["color"],
        )

        ax.scatter(
            sdata["n"],
            sdata["time_us"],
            s=18,
            alpha=0.85,
            color=style["color"],
        )


def add_vertical_legend(fig, axes):
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
    fig.savefig(path, bbox_inches="tight", facecolor="white")
    plt.close(fig)
    print(f"Saved: {path}")


def sanitize_filename(name: str) -> str:
    return (
        name.lower()
        .replace(" ", "_")
        .replace("(", "")
        .replace(")", "")
        .replace("/", "_")
        .replace("__", "_")
    )


# =========================================================
# FIGURE 1:
# focus operation -> 4 panels by pattern
# =========================================================
def plot_focus_operation(df: pd.DataFrame, operation: str):
    sub = df[df["operation"] == operation].copy()
    if sub.empty:
        return

    fig, axes = plt.subplots(2, 2, figsize=(12, 8), sharex=True)
    axes = axes.flatten()

    for ax, pattern in zip(axes, PATTERN_ORDER):
        psub = sub[sub["pattern"] == pattern].copy()
        if psub.empty:
            ax.set_visible(False)
            continue

        add_series(ax, psub)
        ax.set_title(pattern)
        paper_axes(ax)

    add_vertical_legend(fig, axes)
    fig.suptitle(f"{operation}: Performance Across Input Patterns", fontsize=15, y=0.98)

    plt.tight_layout(rect=[0, 0, 0.92, 0.94])
    out = OUTPUT_DIR / f"focus_{sanitize_filename(operation)}.png"
    savefig_clean(fig, out)


# =========================================================
# FIGURE 2:
# single insert only, log scale
# =========================================================
def plot_single_insert_log(df: pd.DataFrame):
    sub = df[df["operation"] == "Single Insert"].copy()
    if sub.empty:
        return

    fig, axes = plt.subplots(2, 2, figsize=(12, 8), sharex=True)
    axes = axes.flatten()

    for ax, pattern in zip(axes, PATTERN_ORDER):
        psub = sub[sub["pattern"] == pattern].copy()
        if psub.empty:
            ax.set_visible(False)
            continue

        add_series(ax, psub)
        ax.set_title(pattern)
        paper_axes(ax)
        ax.set_yscale("log")
        ax.set_ylabel("Time (μs, log scale)")

    add_vertical_legend(fig, axes)
    fig.suptitle("Single Insert: Fine-Grained Comparison (Log Scale)", fontsize=15, y=0.98)

    plt.tight_layout(rect=[0, 0, 0.92, 0.94])
    out = OUTPUT_DIR / "single_insert_log.png"
    savefig_clean(fig, out)


# =========================================================
# FIGURE 3:
# search miss as supplementary figure
# =========================================================
def plot_search_miss(df: pd.DataFrame):
    sub = df[df["operation"] == "Search (miss)"].copy()
    if sub.empty:
        return

    fig, axes = plt.subplots(2, 2, figsize=(12, 8), sharex=True)
    axes = axes.flatten()

    for ax, pattern in zip(axes, PATTERN_ORDER):
        psub = sub[sub["pattern"] == pattern].copy()
        if psub.empty:
            ax.set_visible(False)
            continue

        add_series(ax, psub)
        ax.set_title(pattern)
        paper_axes(ax)

    add_vertical_legend(fig, axes)
    fig.suptitle("Search (miss): Supplementary Comparison", fontsize=15, y=0.98)

    plt.tight_layout(rect=[0, 0, 0.92, 0.94])
    out = OUTPUT_DIR / "search_miss_supplementary.png"
    savefig_clean(fig, out)


# =========================================================
# FIGURE 4:
# summary chart averaged over patterns
# =========================================================
def plot_summary_average(df: pd.DataFrame):
    sub = df[df["operation"].isin(FOCUS_OPERATIONS)].copy()

    grouped = (
        sub.groupby(["operation", "structure", "n"], observed=False)["time_us"]
        .mean()
        .reset_index()
    )

    fig, axes = plt.subplots(2, 2, figsize=(12, 8), sharex=True)
    axes = axes.flatten()

    for ax, operation in zip(axes, FOCUS_OPERATIONS):
        osub = grouped[grouped["operation"] == operation].copy()
        if osub.empty:
            ax.set_visible(False)
            continue

        for structure in STRUCTURE_ORDER:
            sdata = osub[osub["structure"] == structure].sort_values("n")
            if sdata.empty:
                continue

            style = STYLE_MAP[structure]
            ax.plot(
                sdata["n"],
                sdata["time_us"],
                label=structure,
                marker=style["marker"],
                linestyle=style["linestyle"],
                linewidth=style["linewidth"],
                markersize=style["markersize"],
                color=style["color"],
            )
            ax.scatter(
                sdata["n"],
                sdata["time_us"],
                s=18,
                alpha=0.85,
                color=style["color"],
            )

        ax.set_title(operation)
        paper_axes(ax, ylabel="Average time over patterns (μs)")

    add_vertical_legend(fig, axes)
    fig.suptitle("Summary of Key Operations (Averaged Across Input Patterns)", fontsize=15, y=0.98)

    plt.tight_layout(rect=[0, 0, 0.92, 0.94])
    out = OUTPUT_DIR / "summary_key_operations_average.png"
    savefig_clean(fig, out)


# =========================================================
# FIGURE 5:
# compact overview for poster/report appendix
# =========================================================
def plot_overview_compact(df: pd.DataFrame):
    sub = df[df["operation"].isin(FOCUS_OPERATIONS)].copy()

    fig, axes = plt.subplots(4, 1, figsize=(10, 14), sharex=True)
    if len(FOCUS_OPERATIONS) == 1:
        axes = [axes]

    for ax, operation in zip(axes, FOCUS_OPERATIONS):
        osub = (
            sub[sub["operation"] == operation]
            .groupby(["structure", "n"], observed=False)["time_us"]
            .mean()
            .reset_index()
        )

        for structure in STRUCTURE_ORDER:
            sdata = osub[osub["structure"] == structure].sort_values("n")
            if sdata.empty:
                continue

            style = STYLE_MAP[structure]
            ax.plot(
                sdata["n"],
                sdata["time_us"],
                label=structure,
                marker=style["marker"],
                linestyle=style["linestyle"],
                linewidth=style["linewidth"],
                markersize=style["markersize"],
                color=style["color"],
            )

        ax.set_title(operation, loc="left", fontweight="bold")
        paper_axes(ax, ylabel="Avg. time (μs)")

    add_vertical_legend(fig, axes)
    fig.suptitle("Compact Overview of Main Performance Trends", fontsize=15, y=0.995)

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