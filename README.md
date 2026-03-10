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

- **SRP (Single Responsibility Principle)**: `CarController` is moved into its own file and only handles Car-related HTTP requests. `ProductController` only handles Product requests. Before this fix, `CarController` was defined inside `ProductController.java`, which meant one file had two unrelated responsibilities.
- **OCP (Open/Closed Principle)**: `CarService` and `ProductService` are defined as interfaces. If new behavior is needed, a new implementation class can be created without changing the existing service classes.
- **LSP (Liskov Substitution Principle)**: In the `before-solid` version, `CarController extends ProductController`. This is wrong because `CarController` is not a proper substitute for `ProductController`. Car and Product are different domains, so inheritance here breaks the program's correctness. After the fix, `CarController` is a standalone class with no inheritance.
- **ISP (Interface Segregation Principle)**: `CarService` only declares Car-related methods and `ProductService` only declares Product-related methods. Each controller depends on only the interface it actually needs, so no class is forced to depend on methods it does not use.
- **DIP (Dependency Inversion Principle)**: In the `before-solid` version, `CarController` directly injected `CarServiceImpl`, which is a concrete class. After the fix, `CarController` injects `CarService`, which is the interface. This means the high-level controller depends on the abstraction, not the implementation detail.

2. Advantages of applying SOLID principles

- With **SRP**, `CarController` and `ProductController` are in separate files. A bug in Car logic only requires looking at `CarController.java`, with no risk of accidentally breaking Product logic in the same file.
- With **DIP**, `CarController` depends on the `CarService` interface. If the implementation needs to be swapped or mocked in tests, only the bean needs to change and the controller code stays the same.
- With **ISP**, adding a new Car-specific method only affects `CarService`. `ProductService` and all classes depending on it are not touched at all.

3. Disadvantages of not applying SOLID principles

- Without **SRP**, both `ProductController` and `CarController` lived in `ProductController.java`. Any change to Car logic could accidentally affect Product behavior because they shared the same file and class hierarchy.
- Without **LSP**, `CarController extends ProductController` creates a false inheritance relationship. Car requests and Product requests are completely different, so treating a `CarController` as a `ProductController` would lead to incorrect behavior that is hard to trace.
- Without **DIP**, `CarController` was directly coupled to `CarServiceImpl`. Replacing or mocking the service for testing would require editing the controller source code, which increases the chance of introducing new bugs.

---

## Reflection 5:

1. Reflection on TDD flow based on Percival's self-evaluation questions
- The RED-GREEN-REFACTOR workflow was useful because feedback came early from failing tests, especially on validation and update behavior in Order model, repository, and service.
- Test feedback speed for unit tests was still productive, so the cycle stayed practical for iterative changes.
- The tests helped detect both behavior mistakes and edge cases (invalid status, not found ID, author case-sensitivity) before integration-level issues appeared.
- For future work, I should keep the same small-cycle approach, run targeted tests first, and then run the full unit suite before each commit.

2. Reflection on F.I.R.S.T principle in current tests
- **Fast**: mostly satisfied because tests run quickly and stay unit-level.
- **Independent**: mostly satisfied because tests use fresh setup data and mocks per test case.
- **Repeatable**: satisfied because results are deterministic and do not depend on external systems.
- **Self-validating**: satisfied because each test has explicit assertions and clear pass/fail outcomes.
- **Timely**: improved compared to previous workflow because tests were written before implementation in each RED phase.
- Improvement target: reduce duplicated setup data further by extracting reusable test builders/helpers so test intent is easier to read.

---

## Reflection 6:

1. Code smell findings from peer repository review
- `Payment` still had manual accessor methods while Lombok already handled getter/setter generation, so the model had unnecessary duplication.
- `PaymentRepository.findAll()` exposed internal mutable list state directly, which increased risk of accidental external mutation.
- `PaymentServiceImpl` used repeated hardcoded strings for payment methods and statuses, which made behavior harder to maintain and easier to break when adding new paths.
- Voucher method naming was inconsistent (`VOUCHER` vs `VOUCHER_CODE`), so valid voucher flows could be rejected depending on input source.

2. Refactoring performed and impact
- Removed redundant manual accessor duplication in `Payment` and kept Lombok-based accessors.
- Changed `PaymentRepository.findAll()` to return a defensive copy (`new ArrayList<>(paymentData)`).
- Centralized payment method/status values into constants and extracted helper methods (`resolveInitialStatus`, `isVoucherMethod`) to reduce duplicated branching logic.
- Updated voucher handling to accept both `VOUCHER` and `VOUCHER_CODE`, and kept unknown methods rejected by default.
- Updated order status synchronization to use `OrderStatus` enum values for clearer mapping.
- Result: payment flow is easier to read, less fragile against string mismatch bugs, and safer from unintended repository state mutation.
