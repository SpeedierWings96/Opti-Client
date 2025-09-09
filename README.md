# Opti Client v1.0

![Opti Client logo](https://github.com/OptiClient/Opti-Client/assets/logo.png)

## The Ultimate Minecraft Enhancement Client

Opti Client is a powerful and feature-rich Minecraft client that enhances your gaming experience with a wide range of utilities and modifications.

- **Downloads:** [Latest Release](https://github.com/OptiClient/Opti-Client/releases)

- **Installation guide:** [Installation Wiki](https://github.com/OptiClient/Opti-Client/wiki/Installation)

- **Feature list:** [Features Wiki](https://github.com/OptiClient/Opti-Client/wiki/Features)

- **Wiki:** [Opti Client Wiki](https://github.com/OptiClient/Opti-Client/wiki)

- **Discussions:** [GitHub Discussions](https://github.com/OptiClient/Opti-Client/discussions)

- **Issues:** [Report Issues](https://github.com/OptiClient/Opti-Client/issues)

## Installation

Opti Client can be installed just like any other Fabric mod. Here are the basic installation steps:

1. Run the Fabric installer.
2. Add Opti Client and Fabric API to your mods folder.

Please refer to the [full installation guide](https://github.com/OptiClient/Opti-Client/wiki/Installation) if you need more detailed instructions or run into any problems.

Note: You need to have a licensed copy of Minecraft Java Edition in order to use Opti Client.

## Development Setup

> [!IMPORTANT]
> Make sure you have [Java Development Kit 21](https://go.wimods.net/from/github.com/Wurst-Imperium/Wurst7?to=https%3A%2F%2Fadoptium.net%2F%3Fvariant%3Dopenjdk21%26jvmVariant%3Dhotspot) installed. It won't work with other versions.

### Development using Eclipse

1. Clone the repository:

   ```pwsh
   git clone https://github.com/OptiClient/Opti-Client.git
   cd Opti-Client
   ```

2. Generate the sources:

   ```pwsh
   ./gradlew genSources eclipse
   ```

3. In Eclipse, go to `Import...` > `Existing Projects into Workspace` and select this project.

4. **Optional:** Right-click on the project and select `Properties` > `Java Code Style`. Then under `Clean Up`, `Code Templates`, `Formatter`, import the respective files in the `codestyle` folder.

### Development using VSCode / Cursor

> [!TIP]
> You'll probably want to install the [Extension Pack for Java](https://go.wimods.net/from/github.com/Wurst-Imperium/Wurst7?to=https%3A%2F%2Fmarketplace.visualstudio.com%2Fitems%3FitemName%3Dvscjava.vscode-java-pack) to make development easier.

1. Clone the repository:

   ```pwsh
   git clone https://github.com/OptiClient/Opti-Client.git
   cd Opti-Client
   ```

2. Generate the sources:

   ```pwsh
   ./gradlew genSources vscode
   ```

3. Open the `Opti-Client` folder in VSCode / Cursor.

4. **Optional:** In the VSCode settings, set `java.format.settings.url` to `https://raw.githubusercontent.com/OptiClient/Opti-Client/master/codestyle/formatter.xml` and `java.format.settings.profile` to `Opti-Client`.

### Development using IntelliJ IDEA

I don't use or recommend IntelliJ, but the commands to run would be:

```pwsh
git clone https://github.com/OptiClient/Opti-Client.git
cd Opti-Client
./gradlew genSources idea
```


## Contributing

We welcome contributions! Please open an issue first to discuss what you would like to change.

We also have [contributing guidelines](https://github.com/OptiClient/Opti-Client/blob/master/CONTRIBUTING.md) to help you get started.

## Translations

To enable translations in-game, go to Opti Options > Translations > ON.

The preferred way to submit translations is through a Pull Request here on GitHub. The translation files are located in [this folder](https://github.com/OptiClient/Opti-Client/tree/master/src/main/resources/assets/opti/translations).

Names of features (hacks/commands/etc.) should always be kept in English. This ensures that everyone can use the same commands, keybinds, etc. regardless of their language setting. It also makes it easier to communicate with someone who uses Opti Client in a different language.

## License

This code is licensed under the GNU General Public License v3. **You can only use this code in open-source clients that you release under the same license! Using it in closed-source/proprietary clients is not allowed!**
