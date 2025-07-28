# acfdump
Rewamp of the original ACF extraction tool by Barubary.

## Description
ACF files are used in *Pokémon Ranger: Guardian Signs* data. This tool allows you to extract and decompress the files they contain.

The original program was developed by Barubary. To ensure that it is not lost and can continue to be maintained and forked, [I decompiled it](/tree/decompilation), corrected build errors, and made some minor improvements.

For more information about the ACF format, see [the documentation](/tree/main/doc/acf.md).

## Building
1. [Install Java](https://notes.highlysuspect.agency/blog/managing_java/)
2. [Add Java to your PATH](https://www.java.com/en/download/help/path.html) if it is not already done during installation
3. Depending on your operating system or your shell, run the `build.bat` script (Windows), the `build.sh` script (Unix) or the `make` command if it is installed (Unix)

## Usage
### Dumping the ROM
You can dump your own *Pokémon Ranger: Guardian Signs* ROM from:
* [a console from the Nintendo DS or Nintendo 3DS family](https://dumping.guide/carts/nintendo/ds) (Game Card release)
* [a Wii U](https://wiki.hacks.guide/wiki/Wii_U:VC_Extract) (Virtual Console release)

### Getting the ACF files
1. Download, extract and launch [NDSFactory](https://github.com/Luca1991/NDSFactory/releases/latest)
2. Open the program, load your ROM, then press the `Extract Eerything` button and choose where to save your files
3. Once the process is complete, go to the `Fat Tools` tab, fill in the first three fields with the requested files you just extracted (`fat_data.bin`, `fnt.bin` and `fat.bin`), then press the `Extract FAT Data!` button and choose where to save your files
4. Go to your output folder. ACF files are located in the `data` folder

### Running acfdump
* For one specific ACF file, run `java -jar acfdump.jar <filename>.acf`
* For every ACF files in the current folder, run `java -jar acfdump.jar *.acf`

Output files will be located in a folder with the same name as the input ACF file.

## Credits
* Original tool and documentation by [Barubary](https://github.com/Barubary) (available on [his website](http://www.propl.nl/javaprogs/acfdump.jar))
* Decompilation done using [Fernflower](https://github.com/fesh0r/fernflower)
* Rewamped tool by [SombrAbsol](https://github.com/SombrAbsol)
