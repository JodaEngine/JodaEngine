<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<html>
    <head>
        <script type="text/javascript" src="/javascripts/lib/jquery.tools.1.2.5.min.js"></script>
        
        <script type="text/javascript" src="/javascripts/management/navigator-crud.js"></script>
        <script type="text/javascript" src="/javascripts/management/repository-crud.js"></script>
        
        <script type="text/javascript" src="/javascripts/debugger/debugger-helper.js"></script>
        <script type="text/javascript" src="/javascripts/debugger/svg-crud.js"></script>
        <script type="text/javascript" src="/javascripts/debugger/svg.js"></script>
        
        <%-- optional --%>
        <script type="text/javascript" src="/javascripts/lib/jquery.timers-1.2.js"></script>
        
        
        <link rel="stylesheet" type="text/css" href="/stylesheets/jquery.tools/overlay.css">
        <link rel="stylesheet" type="text/css" href="/stylesheets/jquery.ui/ui-lightness/jquery-ui-1.8.12.custom.css">
        
        <title>JodaEngine | Debugger: SVG Artifact Explorer</title>
    </head>
    <body>
        <h1>Debugger: SVG Artifact Explorer</h1>
        
        <h2>SVG Artifacts</h2>
        <table id="definitions-overview" class="svg-overview" width="100%">
            <thead>
                <tr>
                    <th class="loading-data" style="width: 50%">Process Definition</th>
                    <th class="loading-data" style="width: 50%">Controls</th>
                </tr>
            </thead>
            <tbody></tbody>
        </table>
        
        <a href="#" id="definitions-overview-refresh">Refresh table</a>
        
        <div class="dialog" id="set-svg-artifact-dialog" title="Set a svg artifact">
            <form id="set-svg-artifact" method="post" action=""
                    accept="application/xml" enctype="multipart/form-data">
                <input type="file" name="svgArtifact" size="40" />
            </form>
        </div>
        
        <!-- Full SVG Artifact Overlay -->
        <div class="overlay" id="svg-artifact-full-overlay">
            <div class="full-svg-artifact"></div>
        </div>
    </body>
</html>
