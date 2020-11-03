countries = {}
$(document).ready(function(){
	
  
	var raw = ""

	$.ajax({
    url: "data.csv",
    success: function(html) {
      raw = html;
    },
    async:false
  });

 data = raw.split("\n")
	  for(i = 0;i< data.length;i++){
	  		temp = data[i].replace(/\".+\"/g,"")
	  		name = temp.split(",")[3]

	  		//console.log(data[i])
	  		
	  		if (countries[name] == null)
	  			countries[name] = []
	  		
	  		countries[name].push(data[i])
	  		
	  }
console.log(countries)


});
