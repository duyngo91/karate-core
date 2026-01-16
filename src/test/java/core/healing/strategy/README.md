# Healing Strategies Unit Tests

Comprehensive unit test suite for all 9 healing strategies in the Karate Enterprise Framework.

## 📊 Test Coverage Summary

| Strategy | Test File | Test Cases | Coverage Focus |
|----------|-----------|------------|----------------|
| **ExactAttribute** | `ExactAttributeStrategyTest.java` | 15 | Exact matching, similarity scoring, null handling |
| **KeyBased** | `KeyBasedStrategyTest.java` | 16 | Prefix normalization, naming conventions |
| **TextBased** | `TextBasedStrategyTest.java` | 18 | Text matching, tag priorities, attributes |
| **Structural** | `StructuralStrategyTest.java` | 16 | DOM structure, depth, parent, form context |
| **Neighbor** | `NeighborStrategyTest.java` | 15 | Previous sibling matching, context |
| **CrossAttribute** | `CrossAttributeStrategyTest.java` | 13 | Cross-attribute value detection |
| **SemanticValue** | `SemanticValueStrategyTest.java` | 15 | Semantic similarity, synonyms |
| **RagHealing** | `RagHealingStrategyTest.java` | 11 | Vector embeddings, golden state |
| **VisualHealing** | `VisualHealingStrategyTest.java` | 10 | Image similarity, SSIM |
| **TOTAL** | **9 files** | **~129 tests** | **Comprehensive coverage** |

## 🚀 Running Tests

### Run All Strategy Tests
```bash
mvn test -Dtest=HealingStrategiesTestSuite
```

### Run Individual Strategy Test
```bash
# Example: Run only ExactAttributeStrategy tests
mvn test -Dtest=ExactAttributeStrategyTest

# Example: Run only RagHealingStrategy tests
mvn test -Dtest=RagHealingStrategyTest
```

### Run Specific Test Method
```bash
mvn test -Dtest=ExactAttributeStrategyTest#shouldReturnPerfectScoreForExactIdMatch
```

### Run with Coverage Report
```bash
mvn clean test jacoco:report
# Report will be in: target/site/jacoco/index.html
```

## 📝 Test Structure

Each test class follows this pattern:

```java
@DisplayName("StrategyName Tests")
class StrategyNameTest {
    
    private Strategy strategy;
    
    @BeforeEach
    void setUp() {
        strategy = new Strategy();
    }
    
    @Test
    @DisplayName("Should test specific behavior")
    void shouldTestSpecificBehavior() {
        // Given
        ElementNode original = createNode(...);
        ElementNode candidate = createNode(...);
        
        // When
        double score = strategy.score(original, candidate);
        
        // Then
        assertTrue(score > expectedThreshold);
    }
}
```

## 🎯 Test Categories

### 1. Happy Path Tests
- Exact matches
- High similarity scenarios
- Expected behavior verification

### 2. Edge Cases
- Null values
- Empty strings
- Missing attributes
- Different data types

### 3. Negative Tests
- Completely different elements
- Low similarity scenarios
- Invalid inputs

### 4. Integration Tests
- Multiple attributes
- Complex scenarios
- Real-world use cases

## 📋 Test Checklist

For each strategy, tests cover:

- [x] **Basic Functionality**
  - Exact matches return high scores
  - Different elements return low scores
  - Strategy name verification
  - Weight verification
  - Threshold verification

- [x] **Edge Cases**
  - Null handling
  - Empty values
  - Missing attributes
  - Invalid inputs

- [x] **Similarity Variations**
  - High similarity (>0.8)
  - Medium similarity (0.5-0.8)
  - Low similarity (<0.5)

- [x] **Special Features**
  - Case sensitivity
  - Whitespace handling
  - Attribute priorities
  - Context awareness

## 🔧 Adding New Tests

To add a new test:

1. **Create test method**:
```java
@Test
@DisplayName("Should describe what is being tested")
void shouldDescribeWhatIsBeingTested() {
    // Given - Setup test data
    // When - Execute the test
    // Then - Verify results
}
```

2. **Use descriptive names**:
   - Method: `shouldDoSomethingWhenCondition()`
   - Display: Clear, readable description

3. **Follow AAA pattern**:
   - **Arrange**: Set up test data
   - **Act**: Execute the code under test
   - **Assert**: Verify the results

4. **Use helper methods**:
```java
private ElementNode createNode(String tag, String id, String text) {
    ElementNode node = new ElementNode();
    node.setTagName(tag);
    if (id != null) node.addAttribute("id", id);
    if (text != null) node.setText(text);
    return node;
}
```

## 📊 Expected Test Results

All tests should pass with:
- ✅ **Success Rate**: 100%
- ✅ **Execution Time**: < 5 seconds total
- ✅ **No Warnings**: Clean output
- ✅ **No Flaky Tests**: Consistent results

## 🐛 Debugging Failed Tests

If a test fails:

1. **Check the assertion message**:
   ```
   Expected: score > 0.8
   Actual: 0.65
   ```

2. **Enable debug logging**:
   ```java
   System.out.println("Score: " + score);
   System.out.println("Original: " + original);
   System.out.println("Candidate: " + candidate);
   ```

3. **Run single test in debug mode**:
   ```bash
   mvn test -Dtest=StrategyTest#testMethod -X
   ```

## 📈 Coverage Goals

Target coverage for healing strategies:

- **Line Coverage**: > 85%
- **Branch Coverage**: > 80%
- **Method Coverage**: 100%

Current coverage: **To be measured**

## 🔄 Continuous Integration

These tests are designed to run in CI/CD:

```yaml
# Example GitHub Actions
- name: Run Strategy Tests
  run: mvn test -Dtest=HealingStrategiesTestSuite
  
- name: Generate Coverage Report
  run: mvn jacoco:report
  
- name: Upload Coverage
  uses: codecov/codecov-action@v3
```

## 📚 References

- [JUnit 5 Documentation](https://junit.org/junit5/docs/current/user-guide/)
- [AssertJ Documentation](https://assertj.github.io/doc/)
- [Mockito Documentation](https://javadoc.io/doc/org.mockito/mockito-core/latest/org/mockito/Mockito.html)

## 🤝 Contributing

When adding new strategies:

1. Create corresponding test class
2. Add minimum 10 test cases
3. Cover happy path, edge cases, and negatives
4. Update this README
5. Update `HealingStrategiesTestSuite`

---

**Last Updated**: 2026-01-17  
**Total Test Cases**: ~129  
**Maintained By**: Karate Framework Team
