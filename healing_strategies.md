# Healing Strategies Documentation

This document explains the **7 healing strategies** used in the framework's self-healing engine. The engine uses a "Hybrid" approach, combining the speed of the original framework with the intelligence of Advanced Healing.

## Overview

When the framework fails to find an element with the original specific locator (e.g., `//input[@id='username']`), it automatically triggers the **Healing Engine**. The engine analyzes the entire page and scores all potential candidate elements against the *metadata* of the missing element.

The candidates are scored using the following strategies:

| Strategy | Name | Weight | Description |
| :--- | :--- | :--- | :--- |
| **1** | **ExactAttribute** | `1.00` | Matches exact attribute values (e.g., `id="user"` matches `id="user"`). |
| **2** | **KeyBased** | `0.95` | Matches logical keys in different casing (e.g., `txtUsername` matches `username`). |
| **3** | **TextBased** | `0.92` | Matches visible text content, even across different tags (e.g., `Label` matches `Span`). |
| **4** | **CrossAttribute** | `0.90` | Matches values across different attributes (e.g., `name="user"` matches `id="user"`). |
| **5** | **SemanticValue** | `0.85` | Matches similar meanings (e.g., `Log In` matches `Sign In`). |
| **6** | **Neighbor** | `0.80` | **[NEW]** Matches based on surrounding elements (e.g., Input next to "Password" Label). |
| **7** | **Structural** | `0.75` | Matches based on DOM structure (depth, parent tag) when attributes fail. |

---

## Detailed Explanations

### 1. ExactAttribute Strategy (Weight: 1.0)
**The Precision Master.**
Looks for elements that have the **SAME attribute name** with the **SAME value**.
*   **Logic**: "If it looked like a duck before, it's probably still a duck."
*   **Result**: High match on `id`, `name`, or `data-testid`.

### 2. KeyBased Strategy (Weight: 0.95)
**The Developer Mind Reader.**
Normalizes naming conventions (prefixes like `btn`, `txt`, `lbl` or casing like `camelCase`, `snake_case`) to find logical matches.
*   **Logic**: `txtUsername` == `user_name` == `UserName` == `username`.
*   **Example**: `name="txtFirstName"` matches `id="firstName"`.

### 3. TextBased Strategy (Weight: 0.92)
**The Visual Matcher.**
For elements like Buttons, Links, and Labels, the text usually remains stable even if the underlying ID or Class changes.
*   **Logic**: "If it says 'Save', it's the Save button."
*   **Example**: `<button>Submit</button>` matches `<a class="btn">Submit</a>`.

### 4. CrossAttribute Strategy (Weight: 0.90)
**The Attribute Swapper.**
Checks "Identity" attributes against each other, assuming values might shift (e.g., from `name` to `id`).
*   **Logic**: `name="user_email"` matches `data-testid="user_email"`.

### 5. SemanticValue Strategy (Weight: 0.85)
**The AI Simulator.**
Uses fuzzy string matching (Levenshtein, Jaro-Winkler) to find strings that are *almost* the same or mean the same thing.
*   **Logic**: "Close enough is good enough."
*   **Example**: `text="Proceed to Checkout"` matches `text="Proceed to Pay"`.

### 6. Neighbor Strategy (Weight: 0.80)
**The Contextual Locator.**
Uses the context of surrounding elements ("neighbors") to locate the target. Ideally for forms where input fields lose IDs but static Labels remain constant.
*   **Logic**: Checks the element immediately preceding the target (Previous Sibling).
*   **Example**: Finds an input field because it is to the right of a "Password" label.
*   **Outcome**: If found, the engine generates a **Robust Neighbor XPath** (e.g., `//label[text()='Password']/following-sibling::input[1]`) to ensure the element can be found reliably in future runs.

### 7. Structural Strategy (Weight: 0.75)
**The Last Resort.**
When all identifiers change, this looks at *where* the element is in the DOM tree.
*   **Logic**: Compares Tag Name, Depth, and Parent Tag.
*   **Example**: "It's the input inside the third div."

---

## Configuration

You can tune the healing sensitivity in `HealingConfig.java` or via `healing-config.json`.
