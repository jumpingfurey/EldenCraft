# Download verification

The published v0.1.0 release ZIP is `EldenCraft-Windows-Prototype-0.1.0.zip`.

SHA-256:

```text
616edb3ede46602bf8a28dcb7a36e8c66c5958ec0d707d2c7c3be2eae35a8a61
```

[VirusTotal lookup for this exact SHA-256](https://www.virustotal.com/gui/file/616edb3ede46602bf8a28dcb7a36e8c66c5958ec0d707d2c7c3be2eae35a8a61).

**Scan status:** no completed VirusTotal report has been verified. The hash lookup returned “Item not found” when checked on 4 October 2026. Do not interpret the link as a clean scan or a safety guarantee.

To compare your download on Windows:

```powershell
Get-FileHash -Algorithm SHA256 -LiteralPath './EldenCraft-Windows-Prototype-0.1.0.zip'
```

The release includes `SHA256SUMS.txt` for the archive, plus a manifest inside the package for its contents. A matching hash confirms that your file matches this release; it does not establish that software is harmless. Antivirus scan results are one piece of evidence, not proof of complete safety.

Source, dependencies and license notices are available for review. The release ZIP remains unchanged when documentation or showcase assets are updated; a new binary package requires its own hash and scan.
