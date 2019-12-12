<!doctype html>
<html>

<body>

<div id="content" role="main">
    <section class="row colset-2-its">

        <div id="controllers" role="navigation">
            <h2>Available Pumpkins:</h2>
            <ul>
                <g:each var="video" in="${videos}">
                    <li class="controller">
                        <g:link controller="test" action="play" params="[video:video.name.name().toUpperCase()]">${video.name.name()}</g:link>
                    </li>
                </g:each>
            </ul>
        </div>

        <div id="holograms" role="navigation">

            <h2>Available Holograms:</h2>
            <g:link controller="test" action="mute">Mute</g:link> | <g:link controller="test" action="unMute">Un-Mute</g:link> | <g:link controller="test" action="shutdown">Shutdown</g:link>
            <ul>
                <g:each var="hologram" in="${holograms}">
                    <li class="controller">
                        <g:link controller="test" action="play" params="[video:hologram.name.name().toUpperCase()]">${hologram.name.name()}</g:link>
                    </li>
                </g:each>
            </ul>
        </div>

        <div id="jokes" role="navigation">
            <h2>Available jokes:</h2>
            <ul>
                <g:each var="joke" in="${jokes}">
                    <li class="controller">
                        <g:link controller="test" action="play" params="[video:joke.name.name().toUpperCase()]">${joke.name.name()}</g:link>
                    </li>
                </g:each>
            </ul>
        </div>
    </section>
</div>

</body>
</html>
