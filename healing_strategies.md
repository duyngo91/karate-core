# Healing Strategies Documentation

This document explains the **9 healing strategies** used in the framework's self-healing engine. The engine uses a "Hybrid" approach, combining the speed of the original framework with the intelligence of Advanced Healing.

## Overview

When the framework fails to find an element with the original specific locator (e.g., `//input[@id='username']`), it automatically triggers the **Healing Engine**. The engine analyzes the entire page and scores all potential candidate elements against the *metadata* of the missing element.

The candidates are scored using the following strategies:

| Strategy | Name | Weight | Description |
| :--- | :--- | :--- | :--- |
| **1** | **ExactAttribute** | `1.00` | Matches exact attribute values (e.g., `id="user"` matches `id="user"`). |
| **2** | **Location** | `0.60` | **[NEW]** Matches based on geometric proximity (x,y coordinates) compared to the last recorded "Golden State". |
| **3** | **KeyBased** | `0.95` | Matches logical keys in different casing (e.g., `txtUsername` matches `username`). |
| **4** | **TextBased** | `0.92` | Matches visible text content, even across different tags (e.g., `Label` matches `Span`). |
| **5** | **CrossAttribute** | `0.90` | Matches values across different attributes (e.g., `name="user"` matches `id="user"`). |
| **6** | **RAG (Semantic)** | `0.95` | **[NEW]** Matches using AI Vectors. Finds elements with similar "meaning" (text, label, context) even if attributes change. |
| **7** | **SemanticValue** | `0.85` | Matches similar strings using fuzzy matching (e.g., `Log In` matches `Sign In`). |
| **8** | **Neighbor** | `0.80` | Matches based on surrounding elements (e.g., Input next to "Password" Label). |
| **9** | **Structural** | `0.75` | Matches based on DOM structure (depth, parent tag) when attributes fail. |

---

## Detailed Explanations

### 1. ExactAttribute Strategy (Weight: 1.0)
**The Precision Master.**
Looks for elements that have the **SAME attribute name** with the **SAME value**.
*   **Logic**: "If it looked like a duck before, it's probably still a duck."
*   **Result**: High match on `id`, `name`, or `data-testid`.

### 2. Location Healing Strategy (Weight: 0.6)
**The Geographer.**
Uses the `x, y, width, height` coordinates from the stored "Golden State" to find an element that is in the *exact same position* as before.
*   **Logic**: "It looks different, but it's in the same spot."
*   **Benefit**: Extremely useful when IDs change but the layout is stable.
*   **Note**: Works independently from RAG to avoid polluting semantic vectors with coordinates.

### 3. KeyBased Strategy (Weight: 0.95)
**The Developer Mind Reader.**
Normalizes naming conventions (prefixes like `btn`, `txt`, `lbl` or casing like `camelCase`, `snake_case`) to find logical matches.
*   **Logic**: `txtUsername` == `user_name` == `UserName` == `username`.
*   **Example**: `name="txtFirstName"` matches `id="firstName"`.

### 4. TextBased Strategy (Weight: 0.92)
**The Visual Matcher.**
For elements like Buttons, Links, and Labels, the text usually remains stable even if the underlying ID or Class changes.
*   **Logic**: "If it says 'Save', it's the Save button."
*   **Example**: `<button>Submit</button>` matches `<a class="btn">Submit</a>`.

### 5. CrossAttribute Strategy (Weight: 0.90)
**The Attribute Swapper.**
Checks "Identity" attributes against each other, assuming values might shift (e.g., from `name` to `id`).
*   **Logic**: `name="user_email"` matches `data-testid="user_email"`.

### 6. RAG (RagHealingStrategy) (Weight: 0.95)
**The AI Brain.**
Uses Vector Embeddings (MiniLM Model) to understand the *meaning* of an element.
*   **Input**: Tag Name, Visible Text, Attribute Values, and *Neighbor Text*.
*   **Logic**: Converts the element context into a vector and calculates Cosine Similarity.
*   **Benefit**: Can find "Login" button even if it changes to "Sign In" and loses its ID, because they are semantically close.

### 7. SemanticValue Strategy (Weight: 0.85)
**The AI Simulator.**
Uses fuzzy string matching (Levenshtein, Jaro-Winkler) to find strings that are *almost* the same or mean the same thing.
*   **Logic**: "Close enough is good enough."
*   **Example**: `text="Proceed to Checkout"` matches `text="Proceed to Pay"`.

### 8. Neighbor Strategy (Weight: 0.80)
**The Contextual Locator.**
Uses the context of surrounding elements ("neighbors") to locate the target. Ideally for forms where input fields lose IDs but static Labels remain constant.
*   **Logic**: Checks the element immediately preceding the target (Previous Sibling).
*   **Example**: Finds an input field because it is to the right of a "Password" label.
*   **Outcome**: If found, the engine generates a **Robust Neighbor XPath**.

### 9. Structural Strategy (Weight: 0.75)
**The Last Resort.**
When all identifiers change, this looks at *where* the element is in the DOM tree.
*   **Logic**: Compares Tag Name, Depth, and Parent Tag.
*   **Example**: "It's the input inside the third div."

---

## Configuration

You can enable/disable strategies dynamically in `healing-config.json`:

```json
{
    "strategies": [
        "ExactAttributeStrategy",
        "KeyBasedStrategy",
        "LocationHealingStrategy", 
        "RagHealingStrategy",
        ...
    ]
}
```
