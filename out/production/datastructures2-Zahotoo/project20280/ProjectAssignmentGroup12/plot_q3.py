import re
from pathlib import Path

import numpy as np
import pandas as pd
import matplotlib.pyplot as plt
from matplotlib.ticker import ScalarFormatter

# =========================================================
# CONFIGURATION
# =========================================================
INPUT_TXT = "q3_results.txt"
OUTPUT_DIR = Path("q3_paper_figures")
OUTPUT_DIR.mkdir(exist_ok=True)

PATTERN_ORDER = ["Random", "Nearly Sorted", "Reverse Sorted"]
ALGORITHM_ORDER = ["TreapSort", "PQSort", "Collections.sort", "QuickSort", "MergeSort"]

STYLE_MAP = {
    "TreapSort": {
        "marker": "o",
        "linewidth_raw": 1.2,
        "linewidth_trend": 2.2,
        "markersize": 4.6,
        "color": "#ef3b2c",
    },
    "PQSort": {
        "marker": "s",
        "linewidth_raw": 1.2,
        "linewidth_trend": 2.2,
        "markersize": 4.6,
        "color": "#2170b5",
    },
    "Collections.sort": {
        "marker": "^",
        "linewidth_raw": 1.2,
        "linewidth_trend": 2.2,
        "markersize": 4.8,
        "color": "#669aba",
    },
    "QuickSort": {
        "marker": "D",
        "linewidth_raw": 1.2,
        "linewidth_trend": 2.2,
        "markersize": 4.3,
        "color": "#31a354",
    },
    "MergeSort": {
        "marker": "P",
        "linewidth_raw": 1.2,
        "linewidth_trend": 2.2,
        "markersize": 4.5,
        "color": "#756bb1",
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
def parse_q3_output(text: str) -> pd.DataFrame:
    """
    Parse the raw Q3 benchmark output into a tidy DataFrame.
    """
    lines = text.splitlines()
    current_pattern = None
    rows = []

    pattern_re = re.compile(r"Input Pattern:\s*(.+)")
    row_re = re.compile(
        r"^\s*(\d+)\s*\|\s*([0-9.]+)\s*\|\s*([0-9.]+)\s*\|\s*([0-9.]+)\s*\|\s*([0-9.]+)\s*\|\s*([0-9.]+)\s*$"
    )

    for raw_line in lines:
        line = raw_line.rstrip()
        if not line.strip():
            continue

        m_pat = pattern_re.search(line)
        if m_pat:
            current_pattern = m_pat.group(1).strip()
            continue

        stripped = line.strip()
        if (
                ("TreapSort" in line and "MergeSort" in line)
                or set(stripped) == {"-"}
                or set(stripped) == {"="}
                or stripped.startswith("Q3:")
                or stripped.startswith("TreapSort vs")
                or stripped.startswith("Each measurement")
        ):
            continue

        m = row_re.match(line)
        if m and current_pattern is not None:
            n = int(m.group(1))
            treap, pq, java_sort, quick, merge = map(float, m.groups()[1:])

            rows.extend([
                {"pattern": current_pattern, "n": n, "algorithm": "TreapSort", "time_us": treap},
                {"pattern": current_pattern, "n": n, "algorithm": "PQSort", "time_us": pq},
                {"pattern": current_pattern, "n": n, "algorithm": "Collections.sort", "time_us": java_sort},
                {"pattern": current_pattern, "n": n, "algorithm": "QuickSort", "time_us": quick},
                {"pattern": current_pattern, "n": n, "algorithm": "MergeSort", "time_us": merge},
            ])

    df = pd.DataFrame(rows)
    if df.empty:
        raise ValueError("No data parsed. Please check the format of q3_results.txt.")

    df["pattern"] = pd.Categorical(df["pattern"], categories=PATTERN_ORDER, ordered=True)
    df["algorithm"] = pd.Categorical(df["algorithm"], categories=ALGORITHM_ORDER, ordered=True)
    df = df.sort_values(["pattern", "algorithm", "n"]).reset_index(drop=True)

    return df

# =========================================================
# TITLE AND AXIS HELPERS
# =========================================================
def add_bold_suptitle(fig, title, y=0.985, fontsize=15):
    """
    Add a bold figure title without underline.
    """
    fig.suptitle(title, fontsize=fontsize, fontweight="bold", y=y)


def paper_axes(ax, xlabel="Input size n", ylabel="Time (μs)", use_log_x=True):
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
    for algorithm in ALGORITHM_ORDER:
        sdata = df_sub[df_sub["algorithm"] == algorithm].sort_values("n")
        if sdata.empty:
            continue

        style = STYLE_MAP[algorithm]
        x = sdata["n"].to_numpy()
        y = sdata["time_us"].to_numpy()

        # raw dashed line
        ax.plot(
            x,
            y,
            label=algorithm,
            marker=style["marker"],
            linestyle="--",
            linewidth=style["linewidth_raw"],
            markersize=style["markersize"],
            color=style["color"],
            alpha=0.95,
        )

        # fitted straight trend line
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
# FIGURE GENERATORS
# =========================================================
def plot_all_patterns(df: pd.DataFrame):
    """
    One 2x2 figure: one subplot per input pattern.
    """
    fig, axes = plt.subplots(2, 2, figsize=(12, 8), sharex=False)
    axes = axes.flatten()

    for i, pattern in enumerate(PATTERN_ORDER):
        ax = axes[i]
        psub = df[df["pattern"] == pattern].copy()
        if psub.empty:
            ax.set_visible(False)
            continue

        add_series(ax, psub)
        ax.set_title(pattern, fontweight="bold")
        paper_axes(ax, use_log_x=True)

    # hide empty 4th panel
    if len(PATTERN_ORDER) < len(axes):
        for j in range(len(PATTERN_ORDER), len(axes)):
            axes[j].set_visible(False)

    add_vertical_legend(fig, axes)
    add_bold_suptitle(
        fig,
        "Sorting Benchmark: Performance Across Input Patterns",
        y=0.98,
        fontsize=15
    )

    plt.tight_layout(rect=[0, 0, 0.92, 0.94])
    out = OUTPUT_DIR / "sorting_across_patterns.png"
    savefig_clean(fig, out)


def plot_compact_overview(df: pd.DataFrame):
    """
    Compact single figure averaged across input patterns.
    """
    grouped = (
        df.groupby(["algorithm", "n"], observed=False)["time_us"]
        .mean()
        .reset_index()
    )

    fig, ax = plt.subplots(figsize=(10, 5.8))
    add_series(ax, grouped)
    ax.set_title("Average Over All Input Patterns", loc="left", fontweight="bold")
    paper_axes(ax, ylabel="Average time (μs)", use_log_x=True)

    handles, labels = ax.get_legend_handles_labels()
    fig.legend(
        handles,
        labels,
        loc="upper right",
        bbox_to_anchor=(0.995, 0.98),
        ncol=1,
        frameon=False,
    )

    add_bold_suptitle(
        fig,
        "Compact Overview of Sorting Performance",
        y=0.995,
        fontsize=15
    )

    plt.tight_layout(rect=[0, 0, 0.90, 0.94])
    out = OUTPUT_DIR / "compact_overview_sorting.png"
    savefig_clean(fig, out)


def plot_individual_patterns(df: pd.DataFrame):
    """
    One figure per input pattern.
    """
    for pattern in PATTERN_ORDER:
        psub = df[df["pattern"] == pattern].copy()
        if psub.empty:
            continue

        fig, ax = plt.subplots(figsize=(8.8, 5.6))
        add_series(ax, psub)
        ax.set_title(pattern, loc="left", fontweight="bold")
        paper_axes(ax, use_log_x=True)

        handles, labels = ax.get_legend_handles_labels()
        fig.legend(
            handles,
            labels,
            loc="upper right",
            bbox_to_anchor=(0.995, 0.98),
            ncol=1,
            frameon=False,
        )

        add_bold_suptitle(
            fig,
            f"Sorting Benchmark: {pattern}",
            y=0.99,
            fontsize=15
        )

        plt.tight_layout(rect=[0, 0, 0.90, 0.94])
        out = OUTPUT_DIR / f"sorting_{sanitize_filename(pattern)}.png"
        savefig_clean(fig, out)


def plot_treap_vs_pq(df: pd.DataFrame):
    """
    Extra figure focusing on TreapSort vs PQSort only.
    Useful for poster discussion.
    """
    sub = df[df["algorithm"].isin(["TreapSort", "PQSort"])].copy()

    fig, axes = plt.subplots(1, 3, figsize=(15, 4.6), sharex=False, sharey=False)
    if len(PATTERN_ORDER) == 1:
        axes = [axes]

    for ax, pattern in zip(axes, PATTERN_ORDER):
        psub = sub[sub["pattern"] == pattern].copy()
        if psub.empty:
            ax.set_visible(False)
            continue

        add_series(ax, psub)
        ax.set_title(pattern, fontweight="bold")
        paper_axes(ax, use_log_x=True)

    handles, labels = axes[0].get_legend_handles_labels()
    fig.legend(
        handles,
        labels,
        loc="upper right",
        bbox_to_anchor=(0.995, 0.995),
        ncol=1,
        frameon=False,
    )

    add_bold_suptitle(
        fig,
        "TreapSort vs PQSort",
        y=1.02,
        fontsize=15
    )

    plt.tight_layout(rect=[0, 0, 0.92, 0.93])
    out = OUTPUT_DIR / "treapsort_vs_pqsort.png"
    savefig_clean(fig, out)

# =========================================================
# MAIN
# =========================================================
def main():
    text = Path(INPUT_TXT).read_text(encoding="utf-8")
    df = parse_q3_output(text)

    csv_path = OUTPUT_DIR / "q3_sorting_parsed.csv"
    df.to_csv(csv_path, index=False)
    print(f"Saved: {csv_path}")

    plot_all_patterns(df)
    plot_compact_overview(df)
    plot_individual_patterns(df)
    plot_treap_vs_pq(df)

    print("\nAll Q3 figures generated successfully.")


if __name__ == "__main__":
    main()