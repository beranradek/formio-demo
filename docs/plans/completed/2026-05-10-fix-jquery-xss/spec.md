# Spec: Fix Cross-Site Scripting (XSS) Issue in jQuery

## Overview

The project currently bundles jQuery 1.7.1 (`jquery-1.7.1.min.js`), which is affected by a known XSS vulnerability. Affected versions (< 1.12.2) interpret `text/javascript` responses from cross-origin AJAX requests and automatically execute the contents in `jQuery.globalEval`, even without the `dataType` option. The fix is to upgrade to jQuery 3.x (3.7.1 or latest stable 3.x).

## Requirements

1. Replace the bundled `jquery-1.7.1.min.js` with jQuery 3.7.1 (or the latest stable 3.x release).
2. Update `header.jsp` to reference the new jQuery filename.
3. Remove the old `jquery-1.7.1.min.js` file from the repository.
4. Ensure the existing jQuery UI and TDI bundle scripts still load correctly after the upgrade.

## Acceptance Criteria

- No file named `jquery-1.7.1.min.js` exists in the repository.
- A patched jQuery file (version >= 3.4.0) is present in `src/main/resources/webapp/javascripts/`.
- `header.jsp` references the new jQuery file.
- The application continues to load without JavaScript errors from the updated jQuery version.
