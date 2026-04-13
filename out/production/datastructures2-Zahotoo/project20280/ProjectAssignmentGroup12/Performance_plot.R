# =========================================================
# Q2 Performance Plotting in R
# For COMP20280 Data Structures Project
# =========================================================

# Install packages if needed:
# install.packages(c("ggplot2", "dplyr", "readr", "stringr", "tidyr"))

library(ggplot2)
library(dplyr)
library(readr)
library(stringr)
library(tidyr)

# =========================================================
# CONFIG
# =========================================================
input_file <- "q2_results.txt"
output_dir <- "q2_figures_r"

if (!dir.exists(output_dir)) {
  dir.create(output_dir)
}

pattern_order <- c("Random", "Sorted Ascending", "Sorted Descending", "Partially Sorted")
operation_order <- c(
  "Batch Insert",
  "Single Insert",
  "Search (hit)",
  "Search (miss)",
  "Deletion",
  "Traversal"
)
structure_order <- c("Treap", "AVLTreeMap", "TreeMap")

# =========================================================
# PARSER
# =========================================================
parse_q2_output <- function(file_path) {
  lines <- readLines(file_path, warn = FALSE)

  current_pattern <- NA
  current_n <- NA

  rows <- list()
  idx <- 1

  for (line in lines) {
    line_trim <- str_trim(line)

    if (line_trim == "") next

    # Pattern line
    if (str_detect(line_trim, "^Input Pattern:")) {
      current_pattern <- str_replace(line_trim, "^Input Pattern:\\s*", "")
      current_n <- NA
      next
    }

    # Skip separators and headers
    if (str_detect(line_trim, "^=+$") ||
        str_detect(line_trim, "^-+$") ||
        str_detect(line_trim, "^n\\s*\\|") ||
        str_detect(line_trim, "^Q2:") ||
        str_detect(line_trim, "^Treap vs")) {
      next
    }

    # Case 1: line with n
    m1 <- str_match(
      line,
      "^\\s*(\\d+)\\s*\\|\\s*(.*?)\\s*\\|\\s*([0-9.]+)\\s*\\|\\s*([0-9.]+)\\s*\\|\\s*([0-9.]+)\\s*$"
    )

    if (!all(is.na(m1))) {
      current_n <- as.integer(m1[2])
      op <- str_trim(m1[3])
      treap <- as.numeric(m1[4])
      avl <- as.numeric(m1[5])
      treemap <- as.numeric(m1[6])

      rows[[idx]] <- data.frame(pattern=current_pattern, n=current_n, operation=op, structure="Treap", time_us=treap)
      idx <- idx + 1
      rows[[idx]] <- data.frame(pattern=current_pattern, n=current_n, operation=op, structure="AVLTreeMap", time_us=avl)
      idx <- idx + 1
      rows[[idx]] <- data.frame(pattern=current_pattern, n=current_n, operation=op, structure="TreeMap", time_us=treemap)
      idx <- idx + 1
      next
    }

    # Case 2: continuation line without n
    m2 <- str_match(
      line,
      "^\\s*\\|\\s*(.*?)\\s*\\|\\s*([0-9.]+)\\s*\\|\\s*([0-9.]+)\\s*\\|\\s*([0-9.]+)\\s*$"
    )

    if (!all(is.na(m2)) && !is.na(current_n)) {
      op <- str_trim(m2[2])
      treap <- as.numeric(m2[3])
      avl <- as.numeric(m2[4])
      treemap <- as.numeric(m2[5])

      rows[[idx]] <- data.frame(pattern=current_pattern, n=current_n, operation=op, structure="Treap", time_us=treap)
      idx <- idx + 1
      rows[[idx]] <- data.frame(pattern=current_pattern, n=current_n, operation=op, structure="AVLTreeMap", time_us=avl)
      idx <- idx + 1
      rows[[idx]] <- data.frame(pattern=current_pattern, n=current_n, operation=op, structure="TreeMap", time_us=treemap)
      idx <- idx + 1
      next
    }

    # Case 3: continuation line with leading spaces
    m3 <- str_match(
      line,
      "^\\s{2,}(.*?)\\s*\\|\\s*([0-9.]+)\\s*\\|\\s*([0-9.]+)\\s*\\|\\s*([0-9.]+)\\s*$"
    )

    if (!all(is.na(m3)) && !is.na(current_n)) {
      op <- str_trim(m3[2])
      treap <- as.numeric(m3[3])
      avl <- as.numeric(m3[4])
      treemap <- as.numeric(m3[5])

      rows[[idx]] <- data.frame(pattern=current_pattern, n=current_n, operation=op, structure="Treap", time_us=treap)
      idx <- idx + 1
      rows[[idx]] <- data.frame(pattern=current_pattern, n=current_n, operation=op, structure="AVLTreeMap", time_us=avl)
      idx <- idx + 1
      rows[[idx]] <- data.frame(pattern=current_pattern, n=current_n, operation=op, structure="TreeMap", time_us=treemap)
      idx <- idx + 1
      next
    }
  }

  df <- bind_rows(rows)

  if (nrow(df) == 0) {
    stop("No data parsed. Check the txt format.")
  }

  df <- df %>%
    mutate(
      pattern = factor(pattern, levels = pattern_order),
      operation = factor(operation, levels = operation_order),
      structure = factor(structure, levels = structure_order)
    )

  return(df)
}

# =========================================================
# THEME
# =========================================================
theme_academic <- function() {
  theme_minimal(base_size = 12) +
    theme(
      plot.title = element_text(face = "bold", size = 14, hjust = 0.5),
      plot.subtitle = element_text(size = 10, hjust = 0.5),
      strip.text = element_text(face = "bold", size = 11),
      legend.title = element_blank(),
      legend.position = "top",
      panel.grid.minor = element_blank(),
      panel.grid.major = element_line(linewidth = 0.35, colour = "grey80"),
      axis.title = element_text(face = "bold"),
      axis.text = element_text(colour = "black")
    )
}

# =========================================================
# PLOT 1: one figure per operation
# =========================================================
plot_by_operation <- function(df, output_dir) {
  ops <- levels(df$operation)

  for (op in ops) {
    sub <- df %>% filter(operation == op)
    if (nrow(sub) == 0) next

    p <- ggplot(sub, aes(x = n, y = time_us, color = structure, shape = structure, linetype = structure)) +
      geom_line(linewidth = 0.8) +
      geom_point(size = 2.0, alpha = 0.9) +
      facet_wrap(~ pattern, ncol = 2, scales = "free_y") +
      labs(
        title = paste("Q2 Performance Comparison:", op),
        subtitle = "Treap vs AVLTreeMap vs java.util.TreeMap",
        x = "Input size n",
        y = "Time (μs)"
      ) +
      theme_academic()

    if (op == "Single Insert") {
      p <- p + scale_y_log10()
    }

    file_name <- paste0(
      output_dir, "/operation_",
      str_replace_all(tolower(op), "[ ()/]", "_"),
      ".png"
    )

    ggsave(file_name, p, width = 11, height = 7.5, dpi = 300)
    cat("Saved:", file_name, "\n")
  }
}

# =========================================================
# PLOT 2: one figure per pattern
# =========================================================
plot_by_pattern <- function(df, output_dir) {
  pats <- levels(df$pattern)

  for (pat in pats) {
    sub <- df %>% filter(pattern == pat)
    if (nrow(sub) == 0) next

    p <- ggplot(sub, aes(x = n, y = time_us, color = structure, shape = structure, linetype = structure)) +
      geom_line(linewidth = 0.8) +
      geom_point(size = 2.0, alpha = 0.9) +
      facet_wrap(~ operation, ncol = 3, scales = "free_y") +
      labs(
        title = paste("Q2 Performance Comparison:", pat),
        subtitle = "Treap vs AVLTreeMap vs java.util.TreeMap",
        x = "Input size n",
        y = "Time (μs)"
      ) +
      theme_academic()

    file_name <- paste0(
      output_dir, "/pattern_",
      str_replace_all(tolower(pat), "[ ()/]", "_"),
      ".png"
    )

    ggsave(file_name, p, width = 14, height = 8, dpi = 300)
    cat("Saved:", file_name, "\n")
  }
}

# =========================================================
# PLOT 3: summary figures for report
# =========================================================
plot_summary_figures <- function(df, output_dir) {
  focus_ops <- c("Batch Insert", "Search (hit)", "Deletion", "Traversal")

  for (op in focus_ops) {
    sub <- df %>%
      filter(operation == op) %>%
      group_by(structure, n) %>%
      summarise(avg_time_us = mean(time_us), .groups = "drop")

    p <- ggplot(sub, aes(x = n, y = avg_time_us, color = structure, shape = structure, linetype = structure)) +
      geom_line(linewidth = 0.9) +
      geom_point(size = 2.2, alpha = 0.95) +
      labs(
        title = paste("Average Performance Across Input Patterns:", op),
        subtitle = "Mean time over Random / Ascending / Descending / Partially Sorted inputs",
        x = "Input size n",
        y = "Average time (μs)"
      ) +
      theme_academic()

    file_name <- paste0(
      output_dir, "/summary_",
      str_replace_all(tolower(op), "[ ()/]", "_"),
      ".png"
    )

    ggsave(file_name, p, width = 9, height = 6, dpi = 300)
    cat("Saved:", file_name, "\n")
  }
}

# =========================================================
# OPTIONAL: extra clean scatter-only figure
# good for poster if you want a cleaner look
# =========================================================
plot_clean_scatter <- function(df, output_dir) {
  sub <- df %>%
    filter(operation %in% c("Batch Insert", "Search (hit)", "Deletion", "Traversal"))

  p <- ggplot(sub, aes(x = n, y = time_us, color = structure, shape = structure)) +
    geom_point(size = 2.1, alpha = 0.85) +
    geom_smooth(method = "loess", se = FALSE, linewidth = 0.8) +
    facet_grid(operation ~ pattern, scales = "free_y") +
    labs(
      title = "Performance Trends Across Operations and Input Patterns",
      subtitle = "Scatter + smoothed trend lines",
      x = "Input size n",
      y = "Time (μs)"
    ) +
    theme_academic()

  file_name <- paste0(output_dir, "/poster_scatter_overview.png")
  ggsave(file_name, p, width = 14, height = 10, dpi = 300)
  cat("Saved:", file_name, "\n")
}

# =========================================================
# MAIN
# =========================================================
df <- parse_q2_output(input_file)

# Save parsed CSV
write_csv(df, file.path(output_dir, "q2_performance_parsed.csv"))
cat("Saved parsed CSV\n")

# Generate plots
plot_by_operation(df, output_dir)
plot_by_pattern(df, output_dir)
plot_summary_figures(df, output_dir)
plot_clean_scatter(df, output_dir)

cat("\nAll figures generated successfully.\n")