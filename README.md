Github-Issues was a coding exercise--I designed and coded this in about 12 hours over 3 days, start to finish.



http://tarantulatechnology.com/github-issues/javadoc


getIssues version 1.0

Usage: java -jar getIssues.jar [p] owner1/repo1 owner2/repo-2 owner3/repo_3 ...

The optional first argument p instructs the code to return the JSON string in pretty format.

NOTE: GitHub enforces a rate limit of 10 per minute and you will experience it when you enter
many owner/repo entries or you continuously execute too fast.

You can login to GitHub by entering: your_username and your_password
to improve your rate limit. The rate limit is increased to 30.

Set the following environment variables to login to GitHub.

GITHUB_ISSUES_USERNAME=username

GITHUB_ISSUES_PASSWORD=password

Set this environment variable if you want to print job stats at the end.

GITHUB_ISSUES_PRINT_MESSAGES=true

Set this environment variable if you want to ensure you get all
issues available and mitigate the rate limit. This will cause the program to sleep for 30 seconds when it
detects a potential rate limit violation approaching.

GITHUB_ISSUES_PACING=true
