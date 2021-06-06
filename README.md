# TalesTonini.com
This is my personal website, where I add articles about varied stuff.
It is developed in ScalaJS and hosted with Firebase Hosting.

## Developing
```
> sbt
> ~fastOptJS
```
Open the index.html file and note how it updates automatically with code changes.

## Testing locally before deploying
Use script `test_local.sh`.  You may need to login manually first:
```
> firebase login
> firebase serve
```
Now visit the provided local URL.
If that fails, try re-authenticating first:
```
> firebase login --reauth
> firebase serve
```

## Deploying
Prepare to deploy will create a directory with the contents to be deployed.
```
> ./prep_deploy.sh public
> firebase deploy
```

## TODO
~~- Improve the about page with sections about me and the website~~
- Page with terms and conditions / privacy policy
- Likes
- Tweet/LinkedIn a post
- Tags
- JS bundler
~~- Laika~~
~~  - code with braces -> escape braces~~
~~- Home content~~
- Fix loading wheel when incognito
~~- About page -> layout not good for desktop~~
