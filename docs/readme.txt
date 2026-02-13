TopSecret - Command Line File Viewer

OVERVIEW
Command-line utility for viewing mission data files. Files may be encrypted and require deciphering using a cipher key.

USAGE
  java topsecret                    - List available files
  java topsecret [number]           - Display file contents
  java topsecret [number] [keyfile] - Display with alternate key

EXAMPLES
  java topsecret
    01 filea.txt
    02 fileb.txt

  java topsecret 01
    [Deciphered file contents]

  java topsecret 02 ciphers/altkey.txt
    [Contents deciphered with alternate key]

REQUIREMENTS
- Data files must be in data/ directory
- Default cipher key: ciphers/key.txt
- Cipher key format: Two lines of equal length with unique characters

TEAM RESPONSIBILITIES
- Member A: Command Line Interface (CLI)
- Member B: File Handler (file access)
- Member C: Program Controller (coordination)
- Member D: Cipher (deciphering)

BUILD & RUN (MUST RUN THESE FOR THE CODE TO WORK)
  ./gradlew build
  ./gradlew run
  java -cp build/classes/java/main TopSecret (list files)
  java -cp build/classes/java/main TopSecret 01 (Decipher filea.txt with key.txt)
  java -cp build/classes/java/main TopSecret 02 ciphers/key2.txt (Decipher fileb.txt with specific path)
  java -cp build/classes/java/main TopSecret 03 ciphers/key.txt (Decipher filec.txt with key.txt)

TESTING
  ./gradlew test
