# Obfuscator
The obfuscator itself
<br>

## Config
Sample Config: 
```yaml
input: "input.jar"
output: "output.jar" # you can use '%name%' for name of input jar

transformers:
  renamers:
    dictionary: "\\/._0123456789"
    length:  24
    classes: true  # Rename classes
    fields:  true  # Rename fields
```
