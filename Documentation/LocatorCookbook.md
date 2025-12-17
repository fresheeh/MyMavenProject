# Locator Cookbook: 5 Resilient Selector Strategies

This guide details five resilient locator strategies designed to stabilize test automation against dynamic IDs, changing classes, and complex nesting structures often found in modern web applications.

## L1: XPath by Stable Visible Text
### Selector: `//button[normalize-space(text())='Continue Shopping']`

When to use it:
- When interacting with human-readable elements like buttons, links, or error messages.
- The element's ID or class changes frequently, but the text displayed on the screen remains constant.
- **Why it's resilient:** It doesn't care about the element's position, class, or ID; it only cares about the text content. `normalize-space()` helps mitigate issues caused by extra leading or trailing whitespace introduced by the framework.

## L2: CSS Selector by Dynamic ID Prefix
### Selector: `tr[id^='product']`

When to use it:
- When a dynamically generated ID always starts with a predictable, static string (e.g., `session-field-123456`, `session-field-98765`).
- **Why it's resilient:** It only matches the stable part of the ID (session-field-), ignoring the randomly generated, dynamic part that follows.
	- `^=` means "starts with".
	- You can also use `*=` for "contains" or `$=` for "ends with" if those patterns are more stable.

## L3: XPath by ARIA Role and Accessibility Attributes
### Selector: `//a[@aria-label='main content']`
When to use it:
- On modern web applications (React, Angular, Vue) that heavily use WAI-ARIA standards for accessibility.
- When you need to find an element in a specific functional state (e.g., the currently selected tab, an expanded menu item).
- Why it's resilient: ARIA roles and attributes like `aria-selected`, `aria-expanded`, `aria-label`, or `role='dialog'` are tied to the element's function, which is far less likely to change than its presentation attributes (like a CSS class).

## L4: CSS Selector by Multiple, Combined Stable Attributes
### Selector: `div.gw-col[id='desktop-grid-6'][data-order-sm='11']`

When to use it:
- When no single attribute (ID, class, or name) is unique, but the combination of several attributes is guaranteed to be unique within the page.
- When the development team implements custom `data-` attributes (like `data-qa`, `data-testid`) for testing purposes.
- **Why it's resilient:** It demands an exact match on several stable criteria simultaneously, making it extremely specific and less prone to accidental matches if the DOM changes slightly.

## L5: XPath Traversal from a Stable Anchor Element
### Selector: `//h2[text()='Brands']/following-sibling::div//a[contains(., 'Polo')]`

When to use it:
- In complex page sections where the target element is dynamic, but a nearby unique element (like a section header or label) is stable.
- When the target element's visible text is split across multiple nested tags (e.g., `<a>Text` `<span>Count</span></a>`), requiring the use of the string value operator (`.`).
- **Why it's resilient:** It anchors the locator to the stable, visible text (like a header) and then uses XPath axes to jump to the dynamic target. Using `contains(., 'Text')` (the dot operator) is highly resilient as it matches the full aggregated text content of the element and all its descendants.


## L6: Shadow DOM
### Selectors:

When to use it:
- Shadow DOM traversal is appropriate when the element you need to interact with is part of a Web Component.
- Web Components are frequently used for third-party embeds (like chat widgets, payment forms, or complex interactive maps) to prevent their styles and scripts from interfering with the main page.
- **Why it's resilient:** it specifically addresses the isolation barrier imposed by modern component architectures. The primary goal of Shadow DOM is encapsulation, it isolates the element's CSS and DOM from the main document (the Light DOM). Standard Selenium locators are designed to only search the Light DOM. By using `shadowHost.getShadowRoot()`, you explicitly tell Selenium to cross that isolation boundary and provide a specialized search context (`shadowRoot`) where the internal locators (like `L6_SHADOW_ELEMENT`) can function correctly. This makes the test resilient to the element being hidden from the standard DOM tree.

*Note on Shadow DOM: If you encounter a Shadow Root, standard selectors will fail. You must first locate the Shadow Host element (often by ID or unique class) and then use the WebDriver's `getShadowRoot()` method in Java to enter the hidden DOM structure before applying the relevant selector (L1-L5) to elements inside the Shadow Root.
