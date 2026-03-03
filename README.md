# eShop Application
A simple product management system built with Spring Boot.

## Reflection:

1. Meaningful Names for method and variables

2. DRY (Don't Repeat Yourself)
- Reused service methods across controllers

3. Small Functions
- Each method does one thing
- Easy to read and understand

---

## Issues Found & How to Improve

1. Missing Null Checks
```java
@GetMapping("/edit/{productId}")
public String editProductPage(@PathVariable String productId, Model model) {
    Product product = service.findById(productId);
    model.addAttribute("product", product);
    return "editProduct";
}
```
Problem: If product doesn't exist, `product` will be null.

Fix: Add validation:
```java
Product product = service.findById(productId);
if (product == null) {
    return "redirect:/product/list";
}
```

2. Missing Input Validation
- No validation for negative quantities
- No validation for empty product names

Fix: Add validation annotations:


3. No Delete Confirmation

Fix: Add JavaScript confirmation in ProductList.html:
```html
<button type="submit" class="btn btn-danger btn-sm" 
        onclick="return confirm('Delete this product?')">
    Delete
</button>
```

---

## Secure Coding Practices

- Used POST for delete operations
- Used `@PathVariable` for type-safe URL parameters

---

## Reflection 2:

1. Unit testing gives me better confidence when changing code because I can quickly detect regressions.
- The number of tests in a class should follow behavior complexity, not a fixed number.
- A practical target is to cover important paths: happy path, edge cases, and failure cases.
- Code coverage is useful to find untested lines, but it is only a supporting metric.
- Even 100% coverage does not guarantee zero bugs because assertions can still be weak or incomplete.

2. Duplicating setup code in every functional test suite makes the code less clean and harder to maintain.
- Repeated driver initialization, base URL setup, and helper logic are signs of DRY violations.
- This duplication can reduce code quality because one change must be updated in many places.
- A cleaner approach is to extract shared setup into a base test class or reusable helper methods.
- For UI tests, applying Page Object can also reduce duplication and keep test cases focused on behavior.

---

## Reflection 3:

1. Code quality issue(s) fixed and strategy
- PMD detected `UnnecessaryModifier` issues in `ProductService`.
- I fixed the issue by removing redundant `public` modifiers from interface methods.
- The strategy was: run PMD, identify exact violation, apply focused fix, then run PMD again to verify the issue was gone.

2. CI/CD evaluation (Continuous Integration and Continuous Deployment)
- Current workflow already meets Continuous Integration because each push runs automated testing (`ci.yml`) and code analysis (`pmd.yml` and `scorecard.yml`).
- This setup enforces regular integration checks on every code change, so integration quality is continuously verified by automation.
- Current workflow also meets Continuous Deployment because pushes to `main/master` trigger automatic deployment to Koyeb through `deploy-koyeb.yml`, without manual deploy steps.

---

## Reflection 4:

1. SOLID principles applied in this project

- **SRP (Single Responsibility Principle)**: `CarController` is separated into its own file and only handles Car-related HTTP requests. `ProductController` handles only Product requests. Before the fix, `CarController` was defined inside `ProductController.java`, mixing two responsibilities in one file.
- **OCP (Open/Closed Principle)**: `CarService` and `ProductService` are interfaces. New behavior can be added by creating new implementations without modifying existing service classes.
- **LSP (Liskov Substitution Principle)**: In the `before-solid` version, `CarController extends ProductController`, which is wrong because `CarController` cannot substitute `ProductController` meaningfully — a Car page is not a Product page. After the fix, `CarController` is a standalone class with no inheritance, so the substitution issue is removed.
- **ISP (Interface Segregation Principle)**: `CarService` interface only declares Car-related methods, and `ProductService` only declares Product-related methods. Each controller depends only on the interface it needs, with no forced irrelevant methods.
- **DIP (Dependency Inversion Principle)**: In the `before-solid` version, `CarController` injected `CarServiceImpl` directly (a concrete class). After the fix, it injects `CarService` (the interface), so the high-level controller depends on the abstraction, not the concrete implementation.

2. Advantages of applying SOLID principles

- With **SRP**, when a bug exists in `CarController`, I only need to look at `CarController.java` without touching `ProductController.java`. The scope of change is clear and focused.
- With **DIP**, if I want to swap `CarServiceImpl` for a mock or a different implementation in tests, I only need to provide a different bean — the controller code doesn't change at all.
- With **ISP**, if a new feature requires a new Car-specific method, I add it only to `CarService` without polluting `ProductService` and breaking unrelated classes.

3. Disadvantages of not applying SOLID principles

- Without **SRP**, `ProductController.java` contained both `ProductController` and `CarController`. Changing Car logic risked accidentally breaking Product logic because they shared the same file and the same superclass.
- Without **LSP**, `CarController extends ProductController` meant that anywhere `ProductController` was expected, a `CarController` could be passed in — but Car operations and Product operations are unrelated, so this could lead to unexpected behavior and hard-to-trace bugs.
- Without **DIP**, `CarController` was tightly coupled to `CarServiceImpl`. Replacing it for testing or swapping the implementation would require changing the controller source code directly, increasing the risk of introducing new bugs.
