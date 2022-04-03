# [TalesTonini.com](https://talestonini.com)
This is my personal website, where I add articles about varied stuff.
It is developed in ScalaJS and hosted with Firebase Hosting.

## Developing
```
> sbt fastOptJS
> ./test_local.sh
```
~~Open the index.html file and note how it updates automatically with code changes.~~

The above is not true anymore, as the website depends on Firebase components.
Typically, you'll want to continually bundle the app with:
```
> sbt
> ~fastOptJS
```
...and at another terminal window:
```
> ./test_local.sh
```

## Testing locally before deploying
Use script `test_local.sh`.  You may need to login manually first:
```
> firebase login
```
Now visit the provided local URL.
If that fails, try re-authenticating:
```
> firebase login --reauth
```

## Running unit tests
- Tests only pass with the development API key, as the production one has security restrictions.
- Node.js version 17.x.x presents issues running sbt target `fastOptJS::webpack`. To solve, run the following in the
machine used to build the app (run my `sbt-for-headless-browser.sh`):
```
> export NODE_OPTIONS=--openssl-legacy-provider
```
- The `test` target runs with node modules installed locally in the building machine (not the ones bundled for the app);
make sure the app node dependencies match the modules installed locally for reliable test results.
- Run `npm outdated` to check which node module versions you have in the building machine.
- Run `npm install <node-module>@latest` to get the latest version of a node module.
- The `test` target will run tests within a real browser. Make sure the testing machine has the appropriate drivers.
  - On a Mac, you can install drivers with `brew install geckodriver`.
  - On Ubuntu, you can prepare the environment running `prep_test_env_once.sh`, and start sbt with `sbt.sh`.

## Deploying
Use script `deploy.sh`.
```
> ./deploy.sh
```

## Upgrading npm dependencies
All **npm** dependencies are delivered by *Firebase Hosting* (afaik) so far. To upgrade them, refer to the
script-includes in `index.html`. In the [npm](https://www.npmjs.com/) website, search for modules `firebase` and
`firebaseui` to find out their latest versions.

## TODO

### New features
- Likes
- Tweet/LinkedIn a post
- Tags
- ~~JS bundler~~: makes no sense, as the website does not depend on any npm library (that is not delivered by *Firebase
Hosting*)
- ~~Laika~~
  - ~~code with braces -> escape braces~~
- ~~Home content~~
- ~~Version number in footer~~

### Issues
- ~~Missing a page with terms and conditions / privacy policy~~
- ~~Improve the about page with sections about me and the website~~
- ~~Fix loading wheel when incognito~~
- ~~About page -> layout not good for desktop~~
- ~~About page with duplicate content when flipping mobile horizontally~~

### Nice to have
- ~~Open-source the website~~
- ~~Improve delivery of scripts/styles from `index.html` (Firebase ones are fine, I mean all others)~~
- Auto-deploy? with [Deploy to Firebase Hosting](https://github.com/marketplace/actions/deploy-to-firebase-hosting)
