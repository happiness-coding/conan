# Conan

## Getting Started

Please check the following rules:

## Testing

### Test Coverage Requirements
- **Instruction Coverage**: 80% minimum
- **Branch Coverage**: 70% minimum

### Running Tests

#### Unit Tests
```bash
# Run only unit tests
mvn clean test

# Alternatively, use the profile:
mvn clean test -P unit-tests
```

#### Integration Tests
```bash
# Run only integration tests
mvn clean test -P integration-tests
```

#### All Tests
```bash
# Run all tests
mvn clean test -P all-tests

# Alternatively, use the profile:
mvn clean verify -P all-tests
```

### Viewing Test Reports
After running tests, you can view the test reports in the `target/site` directory.
- Unit Test Reports: target/site/unit-tests-report.html
- Integration Test Reports: target/site/integration-tests-report.html
- Coverage Reports: target/site/jacoco/index.html

### Pre-Commit Checks
The project uses git hooks to enforce quality standards. Before committing:  
- CheckStyle: Ensures code adheres to formatting standards
- SpotBugs: Performs static code analysis to find bugs
- Tests: Unit tests should pass

To enable the git hooks
```bash
git config core.hooksPath .githooks
```

### Development Guidelines
When adding or modifying features:
1. Write unit tests for individual components
2. Write integration tests for API endpoints
3. Ensure test coverage meets the required thresholds:
   - 80% instruction coverage
   - 70% branch 
