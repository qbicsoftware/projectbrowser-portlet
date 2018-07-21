window.life_qbic_projectbrowser_samplegraph_ProjectGraph = function() {
	var diagramElement = this.getElement();
	// d3.select(diagramElement).append("svg");
	var rpcProxy = this.getRpcProxy();

	this.onStateChange = function() {
		imagePath = this.getState().imagePath;
		init_graph_circles(this.getState().project);
	}

	var pi = Math.PI;
	var idToSample = {};
	
	function removeAllSpaces(text) {
		return text.replace(/\s/g, "")
	}

	function getTextWidth(text, font) {
		// re-use canvas object for better performance
		var canvas = getTextWidth.canvas
				|| (getTextWidth.canvas = document.createElement("canvas"));
		var context = canvas.getContext("2d");
		context.font = font;
		var metrics = context.measureText(text);
		return metrics.width;
	}

	var width_label = function(label) {
		return getTextWidth(label, "12px arial") + 5;
	}

	/*
	 * var tooltip = d3.select("body") .append("div") .style("position",
	 * "absolute") .style("z-index", "10") .style("visibility", "hidden")
	 * .text("a simple tooltip");
	 */
	var icons = {
		"dna" : "dna.svg",
		"rna" : "rna.svg",
		"peptides" : "peptide.svg",
		"proteins" : "protein.png",
		"smallmolecules" : "mol.png"
	};

	function addBGImages(canvas, data, prefix, size) {
		var base_dir = imagePath; 
			//window.location.href + "VAADIN/themes/d3js/img/"
		canvas.append("defs").selectAll("patterns").data(data).enter().append(
				"pattern").attr('width', size).attr('height', size).attr("id",
				function(d) {
					return prefix + d;
				}).append("image").attr('width', size).attr('height', size)
				.attr("xlink:href", function(d) {
					return base_dir + icons[d];
				});
	}

	function init_graph_circles(samples) {
		var circ_rad = 20;
		var margin = 15;
		var max_Y = 0, max_X = 0;// maximal node positions

		var g = new dagre.graphlib.Graph()
		// Set an object for the graph label
		g.setGraph({});

		// Default to assigning a new object as a label for each new edge.
		g.setDefaultEdgeLabel(function() {
			return {};
		});

		// if(d3.select(diagramElement).select("div")) {
		// d3.select(diagramElement).select("div").remove();
		// }

		var usedSymbols = new Set();
		var noSymbols = new Set();
		for ( var key in samples) {
			var sample = samples[key];
			idToSample[sample.id] = sample;
			// alert(sample.id);
			g.setNode(sample.id, {
				label : sample.name,
				width : circ_rad * 2,
				height : circ_rad * 2
			});
			for (var j = 0; j < sample.childIDs.length; j++) {
				g.setEdge(sample.id, sample.childIDs[j])
			}
			// for(var j = 0; j < sample.children.length; j++) {
			// g.setEdge(sample.id, sample.children[j].id)
			// }
			label = sample.name;
			lowerLabel = removeAllSpaces(label.toLowerCase());
			res = Object.keys(icons).indexOf(lowerLabel);
			if (res === -1) {
				if (label !== "") {
					noSymbols.add(label);
				}
			} else {
				usedSymbols.add(label);
			}
		}

		// var color = d3.scaleOrdinal()
		// .domain(noSymbols)
		// .range(['#f7fbff','#c6dbef','#6baed6','#2171b5','#084594']);
		// var color = d3.scaleOrdinal(d3.schemeCategory20)
		// var color = d3.scaleOrdinal(d3.schemeCategory20b)
		var color = d3.scaleOrdinal(d3.schemeCategory10).domain(noSymbols);
		if (noSymbols.length > 10) {
			var color = d3.scaleOrdinal(d3.schemeCategory20).domain(noSymbols);
		}

		dagre.layout(g);

		var nodes = g.nodes();
		// find maximum coordinates of nodes
		g.nodes().forEach(function(v) {
			var n = g.node(v);
			if (typeof n != 'undefined') {
				if (n.label != "") {
					label = n.label.toLowerCase().replace(" ", "");
					n["y"] = n["y"] + margin; // spacing from top
					n["x"] = n["x"] + margin; // spacing from left
					var x = n["x"];
					var y = n["y"];
					max_X = Math.max(max_X, x);
					max_Y = Math.max(max_Y, y);
				}
			}
		});
		factor = 1;
		circ_rad = circ_rad * factor;
		margin = 15 * factor; // distance from top and left
		var legend_entry_height = circ_rad + 5;
		// guess needed width of graph using 4-digit sample label
		var box_width = factor * max_X + circ_rad + getTextWidth("9999") + 20;
		var box_height = factor * max_Y + (noSymbols.size + usedSymbols.size)
				* (legend_entry_height + 10);

		// normalize with minimum width needed to make the graph look nice
		var min_width = 1000;
		if (box_width < min_width) {
			var x = min_width / box_width;
			box_width = box_width * x;
			box_height = box_height * x;
		}

		var svg = d3.select(diagramElement).append("div").classed(
				"svg-container", true) // container class to make it responsive
		.append("svg")
		// responsive SVG needs these 2 attributes and no width and height attr
		.attr("preserveAspectRatio", "xMinYMin meet").attr("viewBox",
				"0 0 " + box_width + " " + box_height + "")
		// class to make it responsive
		.classed("svg-content-responsive", true);

		addBGImages(svg, Object.keys(icons), "", circ_rad * 2);
		addBGImages(svg, Object.keys(icons), "legend_", circ_rad);
//		console.log(samples);
//		g.edges().forEach(
//				function(e) {
//					console.log(e)
//					var from = e.v;
//					if (samples[from] == "undefined") { // case: empty node (added
//													// to make graph look
//													// better)
//						line = g.edge(e)["points"][0] // first line, coming
//														// out of the bottom of
//														// the node
//						target_x = line["x"];
//						target_y = line["y"];
//						d3.select("svg").append("line").attr("x1",
//								target_x * factor + margin).attr("y1",
//								target_y * factor + margin).attr("x2",
//								target_x * factor + margin).attr("y2",
//								target_y * factor + margin - circ_rad * 2) // invisible
//																			// node
//																			// is
//																			// spanned
//																			// by
//																			// this
//																			// line
//						.attr("stroke-width", 2).attr("stroke", "black");
//					}
//				});

		g.edges().forEach(
				function(e) {
					var points = g.edge(e)["points"];
					for (var i = 1; i < points.length; i++) {
						var one = points[i - 1];
						var two = points[i];
						d3.select("svg").append("line").attr("x1",
								one["x"] * factor + margin).attr("y1",
								one["y"] * factor + margin).attr("x2",
								two["x"] * factor + margin).attr("y2",
								two["y"] * factor + margin).attr(
								"stroke-width", 2).attr("stroke", "black");
					}
				});

		g.nodes().forEach(
				function(v) {
					// if (typeof n != 'undefined') {
					// alert(v);
					var n = g.node(v);
					var data = idToSample[v];
					var label = n.label;
					if (label != "") {
						var lowerLabel = removeAllSpaces(label.toLowerCase());
						var x = n["x"] * factor;
						var y = n["y"] * factor;
						var rad = circ_rad;

						// var w_label = width_label(label);
						// var x_label = x + rad*2 - w_label;
						// main circles
						d3.select("svg").append("circle").attr("cx", x).attr(
								"cy", y).attr("r", rad).style("fill",
								function() {
									if (usedSymbols.has(label)) {
										return "#3494F8";
									} else {
										return color(label);
									}
								})
						// .on("mouseover", function(){
						// d3.select(this).style("fill","blue");
						// return tooltip.style("visibility",
						// "visible").text(n["label"]+": ...samples");})
						// .on("mouseout", function(){
						// d3.select(this).style("fill",color(n.label));
						// return tooltip.style("visibility", "hidden");})
						;
						// circles containing symbols
						if (usedSymbols.has(label)) {
							d3.select("svg").append("circle").attr("cx", x)
									.attr("cy", y).attr("r", rad).attr(
											"stroke", "black").attr("fill",
											"url(#" + lowerLabel + ")")
							// .on("mouseover", function(){
							// d3.select(this).attr("opacity",0.3);
							// return tooltip.style("visibility",
							// "visible").text(n["label"]+": ...samples");})
							// .on("mouseout", function(){
							// d3.select(this).attr("opacity",1);
							// return tooltip.style("visibility", "hidden");})
							;
						}
						// done and missing datasets (angles)
						var angle_done = 360 * data.measuredPercent / 100
						var arc_done = d3.arc().innerRadius(rad).outerRadius(
								rad + rad / 4).startAngle(0).endAngle(
								angle_done * (pi / 180)) // converting from
															// degrees to
															// radians
						var arc_missing = d3.arc().innerRadius(rad)
								.outerRadius(rad + rad / 4).startAngle(
										angle_done * (pi / 180)).endAngle(
										360 * (pi / 180))
						d3.select("svg").append("path").attr("d", arc_done)
								.attr("fill", "green")
								.attr("stroke","black")
								.attr("transform",
										"translate(" + x + "," + y + ")")
								.on('click', function() {
									rpcProxy.onCircleClick(label, data.codes);
								});
						d3.select("svg").append("path").attr("d", arc_missing)
								.attr("fill", "transparent")
								 .attr("stroke","black")
								.attr("transform",
										"translate(" + x + "," + y + ")")
								.on('click', function() {
								      rpcProxy.onCircleClick(label, data.codes);
								});
						// amount
						d3.select("svg").append("text").text(data.amount).attr(
								"font-family", "sans-serif").attr("font-size",
								"14px").attr("stroke", "black").attr(
								"text-anchor", "middle").attr("x", x + 22)
								.attr("y", y - 22);
								//.attr("y", y + 5);
					}
					// }
				});

		// legend
		var legend_x = margin;
		var legend_y = max_Y * factor + circ_rad + 20;
		d3.select("svg").selectAll("legends").data(Array.from(noSymbols))
				.enter().append("circle").attr("cx", legend_x).attr("cy",
						function(d, i) {
							return legend_y + legend_entry_height * i + 10;
						}).attr("r", circ_rad / 2)
				// .attr("stroke", "black")
				.attr("fill", function(d) {
					return color(d);
				});
		d3.select("svg").selectAll("legends").data(Array.from(usedSymbols))
				.enter().append("circle").attr("cx", legend_x).attr(
						"cy",
						function(d, i) {
							return legend_y + legend_entry_height
									* (noSymbols.size + i) + 10;
						}).attr("r", circ_rad / 2)
				// .attr("stroke", "black")
				.attr("fill", "#3494F8");
		d3.select("svg").selectAll("legends").data(Array.from(usedSymbols))
				.enter().append("circle").attr("cx", legend_x).attr(
						"cy",
						function(d, i) {
							return legend_y + legend_entry_height
									* (noSymbols.size + i) + 10;
						}).attr("r", circ_rad / 2)
				// .attr("stroke","black")
				.attr("fill", function(d) {
					var type = removeAllSpaces(d.toLowerCase());
					return "url(#legend_" + type + ")";
				});
		// text
		d3.select("svg").selectAll("legends").data(Array.from(usedSymbols))
				.enter().append("text").text(function(d) {
					return d;
				}).attr("font-family", "sans-serif").attr("font-size", "14px")
				.attr("stroke", "black").attr("text-anchor", "left").attr("x",
						legend_x + margin).attr(
						"y",
						function(d, i) {
							return legend_y + legend_entry_height
									* (noSymbols.size + i) + 15;
						});
		d3.select("svg").selectAll("legends").data(Array.from(noSymbols))
				.enter().append("text").text(function(d) {
					return d;
				}).attr("font-family", "sans-serif").attr("font-size", "14px")
				.attr("stroke", "black").attr("text-anchor", "left").attr("x",
						legend_x + margin).attr("y", function(d, i) {
					return legend_y + legend_entry_height * i + 15;
				});
	}
	;

}