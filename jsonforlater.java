{
  "schemaVersion": 1,
  "id": "yourmodid", 
  "version": "${version}",
  "name": "Your Mod Name",
  "description": "Your mod description.", 
  "authors": [
    "Your Name" 
  ],
  "contact": {
    "sources": "Your GitHub repository URL" 
  },
  "license": "MIT", 
  "icon": "assets/yourmodid/icon.png", 
  "environment": "*",
  "entrypoints": {
    "main": [
      "com.yourname.yourmodid.YourMod" 
    ]
  },
  "mixins": [
    "yourmodid.mixins.json" // optional mixin file.
  ],
  "depends": {
    "fabricloader": ">=0.16.10", // match build.gradle
    "fabric-api": "*",
    "minecraft": "1.21.1"
  }
}
