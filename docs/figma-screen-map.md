# Figma Screen Map

This map records the Figma nodes that define the Storage Clean variant. It is implementation guidance only and is not packaged into the app.

## Figma Source

- File: `清理软件 - Partial file saved 2026 4 3`
- File key: `hFF5yEAc70C0rBbh4n0PLc`
- URL: https://www.figma.com/design/hFF5yEAc70C0rBbh4n0PLc/%E6%B8%85%E7%90%86%E8%BD%AF%E4%BB%B6---Partial-file-saved-2026-4-3?m=dev
- MCP usage: keep this file open in Figma Desktop; use the node IDs below for each screen or state.

## Rules

- Keep device data, ViewModels, repositories, navigation, and effects from the mother project unless a request explicitly changes behavior.
- Fetch the Figma root node context and screenshot before changing a screen.
- Reuse target components and theme tokens before adding new components.
- Model repeated visual states as one component with style variants.

## Screens

| Screen/state | Figma node | Target file | Existing component | New asset | Status |
| --- | --- | --- | --- | --- | --- |
| Onboarding scanning | `56299:4889` | `app/src/main/java/com/aicleanerstorage/spaceclean/feature/onboarding/ui/OnboardingScanScreen.kt` | `OnboardingScanScreen` | `onboarding_scan_illustration.png` | Implemented |
| Onboarding complete | `56203:2276` | `app/src/main/java/com/aicleanerstorage/spaceclean/feature/onboarding/ui/OnboardingScanScreen.kt` | `OnboardingScanScreen` | `onboarding_trash_can.png` | Implemented |
| Home tab | `56203:2376` | `app/src/main/java/com/aicleanerstorage/spaceclean/feature/home/ui/HomeTabContent.kt` | `HomeTabContent` | Pending | Pending |
| Memory usage below one third | `56299:5522` | `app/src/main/java/com/aicleanerstorage/spaceclean/feature/home/ui/HomeTabContent.kt` | Memory card | Pending | Pending |
| Memory usage one third to two thirds | `56299:5492` | `app/src/main/java/com/aicleanerstorage/spaceclean/feature/home/ui/HomeTabContent.kt` | Memory card | Pending | Pending |
| Memory usage above two thirds | `56299:5507` | `app/src/main/java/com/aicleanerstorage/spaceclean/feature/home/ui/HomeTabContent.kt` | Memory card | Pending | Pending |

## Copy and Tokens

- Product name: `Storage Clean`, sourced from `config/product.json`.
- Memory thresholds: `< 1/3` is low, `>= 1/3 && <= 2/3` is medium, `> 2/3` is high.
- Do not add a second product or brand configuration file when an existing target configuration owns the value.
