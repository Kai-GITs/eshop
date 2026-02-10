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