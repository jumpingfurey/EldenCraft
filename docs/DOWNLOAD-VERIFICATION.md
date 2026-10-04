# Download verification

The published v0.1.0 release ZIP is `EldenCraft-Windows-Prototype-0.1.0.zip`.

SHA-256:

```text
616edb3ede46602bf8a28dcb7a36e8c66c5958ec0d707d2c7c3be2eae35a8a61
```

## VirusTotal results

Reports checked on **4 October 2026**. Results can change after reanalysis.

| File | Report | Observed result |
| --- | --- | --- |
| Release ZIP | [VirusTotal](https://www.virustotal.com/gui/file/616edb3ede46602bf8a28dcb7a36e8c66c5958ec0d707d2c7c3be2eae35a8a61) | **1/60**; Elastic: `Malicious (moderate Confidence)` |
| `erbridge_core.dll` | [VirusTotal](https://www.virustotal.com/gui/file/7a7126b3dc89ee10fc2eecf32b215e5e2441ef0f38897aab2162ba0aaaa7e184) | **1/71**; Cynet: `Malicious (score: 100)` |
| `EldenCraftLoader.dll` | [Hash lookup](https://www.virustotal.com/gui/file/412994976144fdde6aadebcf78ab2e4795b0a9f3674899ddad910e620264c083) | Submission attempted; CAPTCHA blocks completion. No completed scan verified. |

**These are not clean scans or proof of safety.** The detections have not been investigated sufficiently to establish false positives. Not every engine completed an analysis. VirusTotal warned at upload that a multi-file archive larger than 3 MB does not provide its normal contained-file scanning, so the ZIP report must not be treated as comprehensive verification of its contents. Individual reports above cover only the named DLLs; the other bundled binaries/JARs have not been individually verified in this check.

Report screenshots: [ZIP](media/virustotal-v0.1.0.jpg), [native core](media/virustotal-core-v0.1.0.jpg).

To compare your download on Windows:

```powershell
Get-FileHash -Algorithm SHA256 -LiteralPath './EldenCraft-Windows-Prototype-0.1.0.zip'
```

The release includes `SHA256SUMS.txt` for the archive, plus a manifest inside the package for its contents. A matching hash confirms that your file matches this release; it does not establish that software is harmless. Antivirus scan results are one piece of evidence, not proof of complete safety.

Source, dependencies and license notices are available for review. The release ZIP remains unchanged when documentation or showcase assets are updated; a new binary package requires its own hash and scan.
