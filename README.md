# fm-common

[![Build](https://github.com/er1c/fm-common/workflows/Continuous%20Integration/badge.svg?branch=main)](https://github.com/er1c/fm-common/actions?query=branch%3Amain+workflow%3A%22Continuous+Integration%22) [![Maven Central](https://maven-badges.herokuapp.com/maven-central/io.github.er1c/fm-common_2.13/badge.svg)](https://maven-badges.herokuapp.com/maven-central/io.github.er1c/fm-common_2.13)

## Documentation

Links:

- [Website](https://er1c.github.io/fm-common/)
- [API documentation](https://er1c.github.io/fm-common/api/)

### Usage

Add this to your `build.sbt`:

```scala
libraryDependencies += "io.github.er1c" %% "fm-common" % "1.0.1"
```

Cross-builds are available for Scala 2.11.12, 2.12.11 and 2.13.3.  Scala.js 1.x supported.

Find out more in the [microsite](https://er1c.github.io/fm-common/).

#### Implicits

[Implicits.scala](core/jvm/src/main/scala/fm/common/Implicits.scala) is provided to extend in your own implicits object.

To import all implicits:

```scala
import fm.common.Implicits._
```

## Contributing

The Type Safe Equals project welcomes contributions from anybody wishing to participate.  All code or documentation that is provided must be licensed with the same license that Type Safe Equals is licensed with (Apache 2.0, see [LICENCE](./LICENSE.md)).

People are expected to follow the [Scala Code of Conduct](./CODE_OF_CONDUCT.md) when discussing Type Safe Equals on GitHub, Gitter channel, or other venues.

Feel free to open an issue if you notice a bug, have an idea for a feature, or have a question about the code. Pull requests are also gladly accepted. For more information, check out the [contributor guide](./CONTRIBUTING.md).

## Copyright

Copyright 2019 [Frugal Mechanic](http://frugalmechanic.com).
Copyright 2020 the fm-common contributors.

## License

All code in this repository is licensed under the Apache License, Version 2.0.  See [LICENCE](./LICENSE.md).
