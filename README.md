# cljs-doom-fire

An implementation of the PSX DOOM fire effect in ClojureScript, as explained at [Fabien Sanglard's article](http://fabiensanglard.net/doom_fire_psx/index.html).

[See Demo](https://blog.cesarolea.com/public/doom-fire/index.html)

## Getting Started

- Clone the repository to your local computer
- Inside the repository directory: `lein fig-dev`
- Browse to `localhost:9500`

### Prerequisites

- Clojure
- leiningen

### Development workflow

The project includes [figwheel-main](https://figwheel.org/), so any changes done to the ClojureScript sources are reloaded in the browser. To start figwheel:

```
lein fig-dev
```

It will start the figwheel process and wait for a connection to start the ClojureScript repl. Navigate to `localhost:9500`, it should connect and at that moment the repl is available.

An nREPL is also included so you can connect your editor and send code for evaluation directly from the source (tested with emacs only). To connect your editor start a Clojure repl:

```
lein repl :headless :port 6666
```

Once started, connect to the repl as usual. In emacs use `cider-connect-clj` (usually bound to C-c M-c) and enter `localhost` and `6666` when prompted for Host and Port respectively.

Once in the Clojure repl evaluate `(start)` and it should start the figwheel process. Navigate to `localhost:9500`, it should connect and at that moment the repl is available.

### Compiling
From the project directory:

```
lein fig-prod
```

It will generate JavaScript with all optimizations turned on. Output is written to `resources/public/cljs-out/prod-main.js`.

## Built With

* [ClojureScript](https://clojurescript.org/)
* [Figwheel](https://figwheel.org/)
* [Quil](http://quil.info/)

## Authors

* **César Olea** - *Initial work* - [Personal Homepage](https://blog.cesarolea.com)

## License

This project is licensed under the CC0 License.

<p xmlns:dct="http://purl.org/dc/terms/" xmlns:vcard="http://www.w3.org/2001/vcard-rdf/3.0#">
  <a rel="license"
     href="http://creativecommons.org/publicdomain/zero/1.0/">
    <img src="http://i.creativecommons.org/p/zero/1.0/88x31.png" style="border-style: none;" alt="CC0" />
  </a>
  <br />
  To the extent possible under law,
  <a rel="dct:publisher"
     href="https://github.com/cesarolea/cljs-doom-fire">
    <span property="dct:title">César Olea</span></a>
  has waived all copyright and related or neighboring rights to
  <span property="dct:title">cljs-doom-fire</span>.
This work is published from:
<span property="vcard:Country" datatype="dct:ISO3166"
      content="MX" about="https://github.com/cesarolea/cljs-doom-fire">
  Mexico</span>.
</p>

## Acknowledgments
* [Fabien Sanglard](http://fabiensanglard.net/) for his explanation on how PSX DOOM fire effect was implemented.
