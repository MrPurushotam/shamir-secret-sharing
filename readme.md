# Shamir Secret Sharing Implementation

A Java implementation of Shamir's Secret Sharing scheme that reconstructs a secret from encoded polynomial points using Lagrange interpolation.

## Overview

This project implements Shamir's Secret Sharing algorithm, a cryptographic method for distributing a secret among a group of participants. The secret can only be reconstructed when a sufficient number of shares are combined together.

### How It Works

1. **Secret Distribution**: A secret is divided into `n` shares using polynomial interpolation
2. **Threshold Reconstruction**: Any `k` shares (where `k ≤ n`) can reconstruct the original secret
3. **Security**: Fewer than `k` shares reveal no information about the secret

## Project Structure

```
├── DecodeYManual.java    # Main implementation with Lagrange interpolation
├── data.json            # Input data containing encoded shares
└── readme.md           # This file
```

## Features

- **Multi-base Decoding**: Handles shares encoded in different number bases (binary, octal, decimal, hexadecimal, etc.)
- **Lagrange Interpolation**: Uses mathematical interpolation to reconstruct the polynomial
- **Robust Error Handling**: Finds the most likely secret when dealing with potentially corrupted data
- **Combination Analysis**: Tests all possible combinations of `k` shares to find the correct secret

## Input Format

The program expects a `data.json` file with the following structure:

```json
{
  "keys": {
    "n": 10,    // Total number of shares
    "k": 7     // Minimum shares needed to reconstruct secret
  },
  "1": {
    "base": "6",
    "value": "13444211440455345511"
  },
  "2": {
    "base": "15", 
    "value": "aed7015a346d63"
  }
  // ... more shares
}
```

Where:
- `n`: Total number of shares available
- `k`: Threshold number of shares required for reconstruction
- Each numbered entry represents a share with:
  - `base`: The number base used for encoding (2-36)
  - `value`: The encoded value as a string

## Compilation and Execution

### Prerequisites
- Java JDK 8 or higher
- A `data.json` file in the same directory

### Running the Program

1. **Compile the Java file:**
   ```bash
   javac DecodeYManual.java
   ```

2. **Run the program:**
   ```bash
   java DecodeYManual
   ```

### Expected Output

```
n = 10, k = 7
Decoded values (x, y coordinates):
x = 1, y = 1234567890123456789 (encoded: 13444211440455345511)
x = 2, y = 9876543210987654321 (encoded: aed7015a346d63)
...

Most likely secret (f(0)) = 28735619723837
```

## Algorithm Details

### 1. Data Parsing
- Reads and parses the JSON input file
- Extracts `n`, `k`, and all share data
- Converts encoded values from their respective bases to decimal

### 2. Coordinate Extraction
- Each share represents a point `(x, y)` on a polynomial
- `x` is the share index (1, 2, 3, ...)
- `y` is the decoded value from the given base

### 3. Lagrange Interpolation
The secret is the value of the polynomial at `x = 0`, calculated using:

```
f(0) = Σ(i=0 to k-1) yi * Π(j=0 to k-1, j≠i) (-xj) / (xi - xj)
```

Where:
- `yi` is the y-coordinate of the i-th point
- `xi, xj` are x-coordinates of the points
- The product runs over all points except the current one

### 4. Error Correction
- Tests all possible combinations of `k` shares
- Uses frequency analysis to find the most commonly occurring secret
- Returns the secret with the highest frequency (most likely correct)

## Key Classes and Methods

### `DecodeYManual`
- **`lagrangeInterpolation()`**: Performs Lagrange interpolation on a set of points
- **`generateCombinations()`**: Generates all possible combinations of k shares from n total shares
- **`findBestSecret()`**: Analyzes all combinations to find the most frequent (likely correct) secret
- **`extractValue()`**: Helper method for parsing JSON data

## Mathematical Background

Shamir's Secret Sharing is based on polynomial interpolation:
- A polynomial of degree `k-1` is uniquely determined by `k` points
- The secret is stored as the constant term (y-intercept) of the polynomial
- Any `k` shares can reconstruct the polynomial and reveal the secret
- Fewer than `k` shares provide no information about the secret

## Example Use Cases

- **Cryptographic Key Storage**: Distribute encryption keys among multiple parties
- **Backup Systems**: Store critical data across multiple locations
- **Multi-signature Systems**: Require multiple parties to authorize transactions
- **Secure Voting**: Distribute voting authority among multiple officials

## Security Considerations

- This implementation uses `BigInteger` for large number arithmetic
- Supports bases 2-36 for flexible encoding schemes  
- Frequency analysis helps identify correct secrets in noisy data
- No share reveals information about the secret individually

## Troubleshooting

### Common Issues

1. **`NumberFormatException`**: Check that all base values are valid for their corresponding encoded values
2. **File not found**: Ensure `data.json` exists in the same directory as the compiled class
3. **Invalid JSON**: Verify the JSON structure matches the expected format

### Debugging Tips

- The program outputs all decoded (x,y) coordinates for verification
- Check that `k ≤ n` in your input data
- Ensure encoded values are valid for their specified bases

## Contributing

Feel free to submit issues and enhancement requests!

## License

This project is available under the MIT License.