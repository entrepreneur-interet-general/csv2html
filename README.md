

# What it does?

csv2html converts plain csv to datatables HTML pages.

**WARNING**: the application creates HTML pages and store them on disk.
If you expose the application to many users, your disk will perhaps
explode.


# Run

    ~$ git clone https://github.com/entrepreneur-interet-general/csv2html
    ~$ cd csv2html/
    ~$ lein run

Then go to <https://localhost:4321>.


# Configure

You can update parameters in `config.edn` or use environment variables:

<table border="2" cellspacing="0" cellpadding="6" rules="groups" frame="hsides">


<colgroup>
<col  class="org-left" />

<col  class="org-left" />

<col  class="org-left" />
</colgroup>
<thead>
<tr>
<th scope="col" class="org-left">Env</th>
<th scope="col" class="org-left">What it defines</th>
<th scope="col" class="org-left">Fallback value</th>
</tr>
</thead>

<tbody>
<tr>
<td class="org-left">CSV2HTML\_EXPORTDIR</td>
<td class="org-left">Export directory for HTML</td>
<td class="org-left">`/tmp/`</td>
</tr>


<tr>
<td class="org-left">CSV2HTML\_PORT</td>
<td class="org-left">The listening port</td>
<td class="org-left">4321</td>
</tr>


<tr>
<td class="org-left">CSV2HTML\_MAXBODY</td>
<td class="org-left">Maximum upload body size</td>
<td class="org-left">100M</td>
</tr>
</tbody>
</table>

For example:

    ~$ CSV2HTML_PORT="1234" lein run

will run the application at <https://localhost:1234>

    ~$ CSV2HTML_PORT="1234" java -jar csv2html-x.x.x-standalone.jar

will do the same while running the application from a jar file.


# Install


## Deploying the .jar

    ~$ git clone https://github.com/entrepreneur-interet-general/csv2html
    ~$ cd csv2html/
    ~$ lein uberjar

Then deploy `target/uberjar/csv2html-x.x.x-standalone.jar` anywhere you
want with `java -jar csv2html-x.x.x-standalone.jar`.


## Deploying a container

    ~$ git clone https://github.com/entrepreneur-interet-general/csv2html
    ~$ cd csv2html/
    ~$ docker build -t me/csv2html .

will build the container which you can run with

    ~$ CSV2HTML_EXPORTDIR="/exports/" docker run -p 4321:4321 me/csv2html

Note that `CSV2HTML_EXPORTDIR="/exports/"` is quite mandatory here,
otherwise your container will store HTML pages in `/tmp/` and lose them
after a while.


# Todo

-   Allow to transpose csv tables


# License

`csv2html` is licensed under the [Eclipse Public License 1.0](http://www.eclipse.org/legal/epl-v10.html), the
same as Clojure.

