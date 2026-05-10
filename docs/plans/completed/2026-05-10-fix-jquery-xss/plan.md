# Plan: Fix jQuery XSS Vulnerability

## Steps

1. Download jQuery 3.7.1 minified from the official CDN into `src/main/resources/webapp/javascripts/jquery-3.7.1.min.js`.
2. Delete `src/main/resources/webapp/javascripts/jquery-1.7.1.min.js`.
3. Update `src/main/resources/webapp/WEB-INF/jsp/header.jsp` line 26: change script `src` from `jquery-1.7.1.min.js` to `jquery-3.7.1.min.js`.

## Files Changed

- `src/main/resources/webapp/javascripts/jquery-1.7.1.min.js` — deleted
- `src/main/resources/webapp/javascripts/jquery-3.7.1.min.js` — added
- `src/main/resources/webapp/WEB-INF/jsp/header.jsp` — updated script src reference
