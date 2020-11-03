var top_nav_height = $("#navbar-Top").height();

     var width = $("body").width() - 400,
       height = $(document).height() - top_nav_height - 15,
       rotate = 60, // so that [-60, 0] becomes initial center of projection
       maxlat = 83; // clip northern and southern poles (infinite in mercator)


     var projection = d3.geo.mercator()
       .rotate([rotate, 0])
       .center([13, 52])
       .scale(0.6) // we'll scale up to match viewport shortly.
       .translate([width / 2, height / 2])


     // find the top left and bottom right of current projection
     function mercatorBounds(projection, maxlat) {
       var yaw = projection.rotate()[0],
         xymax = projection([-yaw + 180 - 1e-6, -maxlat]),
         xymin = projection([-yaw - 180 + 1e-6, maxlat]);

       return [xymin, xymax];
     }

     // set up the scale extent and initial scale for the projection
     var b = mercatorBounds(projection, maxlat),
       s = width / (b[1][0] - b[0][0]),
       scaleExtent = [s, 10 * s];

     projection
       .scale(scaleExtent[0]);

     var zoom = d3.behavior.zoom()
       .scaleExtent(scaleExtent)
       .scale(projection.scale())
       .translate([0, 0]) // not linked directly to projection
       .on("zoom", redraw);

     var path = d3.geo.path()
       .projection(projection);

     var svg = d3.selectAll('body')
       .append('svg')
       .attr('width', width)
       .attr('height', height)
       .attr("class", "map")
       .call(zoom);

     

     var spider = d3.selectAll('#spiderChart')
     	.append('svg')
     	.attr('width', $("#spiderChart").width())
       	.attr('height', 300)


     //for tooltip 
     var offsetL = document.getElementById('map').offsetLeft + 10;
     var offsetT = document.getElementById('map').offsetTop + 10;

     var path = d3.geo.path()
       .projection(projection);

     var tooltip = d3.select("#map")
       .append("div")
       .attr("class", "tooltip hidden");

     d3.json("world-50m.json", function ready(error, world) {

       svg.append("g")
         .attr("class", "boundary")
         .selectAll("boundary")
         .data(topojson.feature(world, world.objects.countries).features)
         .enter().append("path")
         .attr("name", function (d) {
           return d.properties.name;
         })
         .attr("id", function (d) {
           return d.properties.iso_a3;
         })
         .on('click', selected)
         .on("mousemove", showTooltip)
         .on("mouseout", function (d, i) {
           tooltip.classed("hidden", true);
         })
         .attr("d", path);




       svg.selectAll('path')
         .data(topojson.feature(world, world.objects.countries).features)
         .enter().append('path')

         svg.append("g")
          .attr("class", "bubble")
        .selectAll("circle")
          .data(topojson.feature(world, world.objects.countries).features)
        .enter().append("circle")
          .attr("transform", function(d) { return "translate(" + path.centroid(d) + ")"; })
          .attr("r", 0);

       redraw(); // update path data
     });


     function selectedBar(){
     	d3.select('.selectedBar').classed('selectedBar', false);
       d3.select(this).classed('selectedBar', true);
       //console.log(this.__data__)
        temp = this.__data__.replace(/\".+\"/g,"")
        temp = temp.replace(/-,/g,"0,")
        console.log(temp)
        temp = temp.split(",")
        
       obj = [
       		{axis:"Academic", value:temp[9]/100},
       		{axis:"Employer", value:temp[11]/100},
       		{axis:"Faculty", value:temp[13]/100},
       		{axis:"Citations", value:temp[15]/100},
       		{axis:"Intern Faculty", value:temp[17]/100},
       		{axis:"Intern Students", value:temp[19]/100}
       			]
       var data = []
       data.push(obj)
       $("#spiderChart").empty()
       var spider = d3.selectAll("#spiderChart")
       .append("div")
       .attr("class","radarChart")
       var color = d3.scale.ordinal()
				.range(["#EDC951"]);

			var margin = {top: 40, right: 40, bottom: 40, left: 40}
			width = $("#spiderChart").width() - margin.left - margin.right,
			height = Math.min(width, $("#spiderChart").height() - margin.top - margin.bottom - 20);
			var radarChartOptions = {
			  w: width,
			  h: height,
			  margin: margin,
			  maxValue: 1,
			  levels: 5,
			  roundStrokes: true,
			  color: color
			};
			//Call function to draw the Radar chart
	   RadarChart(".radarChart", data, radarChartOptions);


     }

     function selected() {
       d3.select('.selected').classed('selected', false);
       d3.select(this).classed('selected', true);
       var ylabel = -7;
       var yrect = 0;
       var yvalue = -7
       $("#barChart").empty()
       $("#spiderChart").empty()
       data = countries[this.__data__.properties.name]
       //console.log(data)

       var bar = d3.selectAll('#barChart')
     	.append('svg')
     	.attr('width', $("#barChart").width() )
       	.attr('height', 300)
       	
       var x = d3.scale.linear()
            .range([0, $("#barChart").width()])
            .domain([0, 100]);
           

       

       bar.append("g")
            .attr("class", "bar")
            .selectAll("rect")
           	.data(data)
           	.enter().append("rect")
            .attr("y", function (d) {

            		
            		//console.log(d.split(",")[21])
            	yrect+=25;
            	
            	if (yrect > $("#barChart").height()-5){
            		
            		return -100;
            	}
                return yrect-20;
            })

             .attr("name", function (d) {
             	
	  			name = d.split(",")[2]
	           return name;
	         })
	          .attr("value", function (d) {
	          temp = d.replace(/\".+\"/g,"")
	  			name = temp.split(",")[3]

	           return name;
	         })
            .attr("height", 20)
            .attr("x", function(d){
            	return 110
            })
            .on('click', selectedBar)
            .attr("width", function (d) {
            	if (d.split(",")[21].split("-")[0] > 100)
            		return d.split(",")[22].split("-")[0]
                return d.split(",")[21].split("-")[0]*2;
            });

         bar.append("g")
            .attr("class", "barvalues")
            .attr("width",100)
            .selectAll("text")
            .data(data)
            .enter().append("text")
            //y position of the label is halfway down the bar

            .attr("y", function (d) {
            	yvalue+=25
            	if (yvalue > $("#barChart").height()-13)
            		return -100;
                return yvalue;
            })
            //x position is 3 pixels to the right of the bar
            .attr("x", function (d) {
                return 120 ;
            })
            .text(function (d) {
                
	  		if (d.split(",")[21].split("-")[0] > 100)
            		return d.split(",")[22].split("-")[0]
                return d.split(",")[21].split("-")[0]*2;
            });

        bar.append("g")
            .attr("class", "label")
            .attr("width",100)
            .selectAll("text")
            .data(data)
            .enter().append("text")
            //y position of the label is halfway down the bar

            .attr("y", function (d) {
            	ylabel+=25
            	if (ylabel > $("#barChart").height()-13)
            		return -100;
                return ylabel;
            })
            //x position is 3 pixels to the right of the bar
            .attr("x", function (d) {
                return  ;
            })
            .text(function (d) {
                
	  		name = d.split(",")[2]
	  		if (name.length > 25)
	  			return name.slice(0,20)+"..."
	  		
	         return name;
            });


     }

     function showValue(){

     }
     function showTooltip(d) {
       label = d.properties.name;
       var mouse = d3.mouse(svg.node())
         .map(function (d) {
           return parseInt(d);
         });
       tooltip.classed("hidden", false)
         .attr("style", "left:" + (mouse[0] + offsetL) + "px;top:" + (mouse[1] + offsetT) + "px")
         .html(label);
     }


     var tlast = [0, 0],
       slast = null;

     function redraw() {
     	if (countries["Portugal"] == null){
     		setTimeout(function(){ redraw() }, 100);
     		return 0
     	}
       if (d3.event) {
         var scale = d3.event.scale,
           t = d3.event.translate;

         // if scaling changes, ignore translation (otherwise touch zooms are weird)
         if (scale != slast) {
           projection.scale(scale);
         } else {
           var dx = t[0] - tlast[0],
             dy = t[1] - tlast[1],
             yaw = projection.rotate()[0],
             tp = projection.translate();

           // use x translation to rotate based on current scale
           projection.rotate([yaw + 360. * dx / width * scaleExtent[0] / scale, 0, 0]);
           // use y translation to translate projection, clamped by min/max
           var b = mercatorBounds(projection, maxlat);
           if (b[0][1] + dy > 0) dy = -b[0][1];
           else if (b[1][1] + dy < height) dy = height - b[1][1];
           projection.translate([tp[0], tp[1] + dy]);
         }
         // save last values.  resetting zoom.translate() and scale() would
         // seem equivalent but doesn't seem to work reliably?
         slast = scale;
         tlast = t;
       }

       svg.selectAll('path') // re-project path data
         .attr('d', path);

      svg.selectAll("circle")
          .attr("r", function(d) {
          	//console.log(countries[d.properties.name]);
          	if (countries[d.properties.name] == null)
          		return 1;
          	if (countries[d.properties.name].length == 0)
          		return 0
          	return countries[d.properties.name].length/2; 
      		})
          .attr("transform", function(d) { return "translate(" + path.centroid(d) + ")"; })

     }
