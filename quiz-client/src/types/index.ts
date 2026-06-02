:root {
  --primary-color: #1a73e8;
  --primary-hover: #1557b0;
  --error-color: #d93025;
  --success-color: #188038;
  --warning-color: #f29900;

  --text-primary: #202124;
  --text-secondary: #5f6368;
  --text-disabled: #9aa0a6;

  --bg-primary: #ffffff;
  --bg-secondary: #f8f9fa;
  --bg-hover: #f1f3f4;
  --bg-card: #ffffff;

  --border-color: #dadce0;
  --border-light: #e8eaed;
  --shadow-sm: 0 1px 2px 0 rgba(0,0,0,0.05);
  --shadow-md: 0 1px 3px 0 rgba(0,0,0,0.1), 0 1px 2px -1px rgba(0,0,0,0.1);
  --shadow-lg: 0 4px 6px -1px rgba(0,0,0,0.1), 0 2px 4px -2px rgba(0,0,0,0.1);
  --input-bg: #ffffff;
  --input-border: #dadce0;
  --button-text: #ffffff;
  --button-bg-primary: #1a73e8;
  --button-bg-danger: #dc3545;
  --button-bg-warning: #ffc107;
  --button-text-warning: #333;
}

[data-theme="dark"] {
  --primary-color: #6c8c9e;
  --primary-hover: #7e9eaf;
  --error-color: #b87c7c;
  --success-color: #7c9c7c;
  --warning-color: #c4a47c;

  --text-primary: #e0e0e0;
  --text-secondary: #a0a0a0;
  --text-disabled: #707070;

  --bg-primary: #1a1a1a;
  --bg-secondary: #121212;
  --bg-hover: #2a2a2a;
  --bg-card: #222222;

  --border-color: #333333;
  --border-light: #2a2a2a;
  --input-bg: #222222;
  --input-border: #3a3a3a;
  --button-text: #e0e0e0;
  --button-bg-primary: #4a6a7a;
  --button-bg-danger: #6a4a4a;
  --button-bg-warning: #6a5a4a;
  --button-text-warning: #e0e0e0;
}

* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
}

body {
  font-family: 'Google Sans', 'Roboto', -apple-system, BlinkMacSystemFont, 'Segoe UI', sans-serif;
  background-color: var(--bg-secondary);
  color: var(--text-primary);
  transition: background-color 0.3s ease, color 0.3s ease;
}

.google-input {
  background-color: var(--input-bg);
  border: 1px solid var(--input-border);
  color: var(--text-primary);
  padding: 12px 16px;
  border-radius: 8px;
  font-size: 16px;
  transition: all 0.2s ease;
}

.google-input:focus {
  outline: none;
  border-color: var(--primary-color);
  box-shadow: 0 0 0 2px rgba(108, 140, 158, 0.2);
}

.google-card {
  background-color: var(--bg-card);
  border: 1px solid var(--border-light);
  border-radius: 12px;
  transition: box-shadow 0.2s ease;
}

.google-card:hover {
  box-shadow: var(--shadow-md);
}

.google-btn {
  padding: 8px 16px;
  border-radius: 24px;
  font-weight: 500;
  font-size: 14px;
  cursor: pointer;
  transition: all 0.2s ease;
  background: none;
  border: none;
}

.google-btn-primary {
  background-color: var(--button-bg-primary);
  color: var(--button-text);
}

.google-btn-primary:hover {
  opacity: 0.85;
}

.google-btn-secondary {
  border: 1px solid var(--border-color);
  color: var(--text-primary);
  background: none;
}

.google-btn-secondary:hover {
  background-color: var(--bg-hover);
}

.google-btn-danger {
  background-color: var(--button-bg-danger);
  color: var(--button-text);
}

.google-btn-danger:hover {
  opacity: 0.85;
}

.google-btn-warning {
  background-color: var(--button-bg-warning);
  color: var(--button-text-warning);
}

.google-btn-warning:hover {
  opacity: 0.85;
}

nav, .google-navbar {
  background-color: var(--bg-primary) !important;
  border-bottom: 1px solid var(--border-color);
}

button {
  font-family: inherit;
}

textarea, select {
  background-color: var(--input-bg);
  border: 1px solid var(--input-border);
  color: var(--text-primary);
  padding: 10px;
  border-radius: 8px;
}

.quiz-card {
  background-color: var(--bg-card);
  border: 1px solid var(--border-light);
  border-radius: 12px;
  padding: 20px;
  margin-bottom: 16px;
}

[data-theme="dark"] .google-input:focus {
  box-shadow: 0 0 0 2px rgba(108, 140, 158, 0.3);
}

[data-theme="dark"] a {
  color: var(--text-secondary);
}

[data-theme="dark"] a:hover {
  color: var(--text-primary);
}

[data-theme="dark"] .tag {
  background-color: #2a2a2a;
  color: #b0b0b0;
}

[data-theme="dark"] .published {
  background-color: rgba(124, 156, 124, 0.15);
  color: #a0c0a0;
}

[data-theme="dark"] .draft {
  background-color: rgba(160, 140, 100, 0.15);
  color: #c0b0a0;
}