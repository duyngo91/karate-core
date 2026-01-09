# Healing Strategies Documentation

This document explains the 6 healing strategies used in the framework's self-healing engine. The engine uses a "Hybrid" approach, combining the speed of the original framework with the intelligence of Advanced Healing.

## Overview

When the framework fails to find an element with the original specific locator (e.g. `//input[@id='username']`), it automatically triggers the **Healing Engine**. The engine analyzes the entire page and scores all potential candidate elements against the *metadata* of the missing element (captured during previous successful runs or inferred from the locator).

The candidates are scored using the following strategies:

| Strategy | Name | Weight | Description |
| :--- | :--- | :--- | :--- |
| **1** | **ExactAttribute** | `1.00` | Matches exact attribute values (e.g. `id="user"` matches `id="user"`). |
| **2** | **KeyBased** | `0.95` | Matches logical keys in different casing (e.g. `txtUsername` matches `username`). |
| **3** | **TextBased** | `0.92` | Matches visible text content, even across different tags (e.g. `Label` matches `Span`). |
| **4** | **CrossAttribute** | `0.90` | Matches values across different attributes (e.g. `name="user"` matches `id="user"`). |
| **5** | **SemanticValue** | `0.85` | Matches similar meanings (e.g. `Log In` matches `Sign In`). |
| **6** | **Structural** | `0.75` | Matches based on DOM structure (depth, parent tag) when attributes fail. |

---

## Detailed Explanations

### 1. ExactAttribute Strategy (Weight: 1.0)
**The Precision Master.**
This strategy looks for elements that have the **SAME attribute name** with the **SAME value** (or very similar value).

*   **Logic**: "If it looked like a duck before, it's probably still a duck."
*   **Example**:
    *   Original: `<input id="submit_btn" class="btn-primary">`
    *   Candidate: `<input id="submit_btn" class="btn-secondary">`
    *   **Result**: High match on `id`, partial match on `class`.

### 2. KeyBased Strategy (Weight: 0.95)
**The Developer Mind Reader.**
Developers often use standard naming conventions (prefixes like `btn`, `txt`, `lbl` or casing like `camelCase`, `snake_case`). This strategy normalizes these keys to find the logical match.

*   **Logic**: `txtUsername` == `user_name` == `UserName` == `username`.
*   **Example**:
    *   Original: `name="txtFirstName"`
    *   Candidate: `id="firstName"`
    *   **Result**: Match! The strategy strips `txt` and sees `FirstName` matches `firstName`.

### 3. TextBased Strategy (Weight: 0.92)
**The Visual Matcher.**
For elements like Buttons, Links, and Labels, the text usually remains stable even if the underlying ID or Class changes.

*   **Logic**: "If it says 'Save', it's the Save button."
*   **Example**:
    *   Original: `<button>Submit Order</button>`
    *   Candidate: `<a class="btn">Submit Order</a>`
    *   **Result**: Exact text match, even though tag changed from `button` to `a`.

### 4. CrossAttribute Strategy (Weight: 0.90)
**The Attribute Swapper.**
Modern frameworks often shift values between attributes (e.g. what was a `name` becomes a `data-testid` or `id`). This strategy checks "Identity" attributes against each other.

*   **Logic**: `name` value might have moved to `id` or `data-testid`.
*   **Example**:
    *   Original: `name="user_email"`
    *   Candidate: `data-testid="user_email"`
    *   **Result**: Match! It recognizes both are "Identity" attributes.

### 5. SemanticValue Strategy (Weight: 0.85)
**The AI Simulator.**
Uses fuzzy string matching (Levenshtein distance, Jaccard similarity) to understand that two strings are *almost* the same or mean the same thing.

*   **Logic**: "Close enough is good enough."
*   **Example**:
    *   Original: `text="Proceed to Checkout"`
    *   Candidate: `text="Proceed to Pay"`
    *   **Result**: High semantic score due to shared words and meaning.

### 6. Structural Strategy (Weight: 0.75)
**The Last Resort.**
When all identifiers change (dynamic IDs, no text), this strategy looks at *where* the element is.

*   **Logic**: "It's the input inside the third div."
*   **Example**:
    *   Original: `<div class="container"><input type="password"></div>` (Depth: 5)
    *   Candidate: `<div class="wrapper"><input type="password"></div>` (Depth: 5)
    *   **Result**: Matches based on `parentTag` (div) and `type` (password) and `depth`.

## Configuration

You can tune the healing sensitivity in `HealingConfig.java` or via `healing-config.json`:

```json
{
  "minSimilarityThreshold": 0.5,
  "attributes": [
    { "name": "id", "weight": 1.0 },
    { "name": "data-testid", "weight": 0.9 },
    { "name": "name", "weight": 0.8 }
  ]
}
```
