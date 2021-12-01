# Build & Release

## Dependency updates

Display dependency updates:

```sh
mvn versions:display-parent-updates versions:display-property-updates -U
```

Update dependencies:

```sh
mvn versions:update-parent versions:update-properties
```

## Release locally

Run:

```sh
mvn release:prepare release:perform release:clean -B -DreleaseProfiles=oss-release
```
