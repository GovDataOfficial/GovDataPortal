// Script to create friendlyurls for searchresults

var header = '<?xml version="1.0"?>\n' +
'<!DOCTYPE routes PUBLIC "-//Liferay//DTD Friendly URL Routes 6.2.0//EN"\n' +
'  "http://www.liferay.com/dtd/liferay-friendly-url-routes_6_2_0.dtd">\n' +
'  <!-- We have to introduce MANY fallbacks, because liferay removes the trailing / before matching -->\n' +
'<routes>';
var footer = "</routes>";

var parameters = ['q', 'filter', 'sort', 'boundingbox'];
var parametersRegexval = ['q', 'f', 's', 'boundingbox'];

console.log(header);

function prepareArrays(combination) {
  var parin = [];
  var parout = [];

  for(var parindex=0; parindex<combination.length; parindex++) {
    if(combination.charAt(parindex) === '1') {
      parin.push({'urlkey': parameters[parindex], 'regexval': parametersRegexval[parindex]});
    } else {
      parout.push(parameters[parindex]);
    }
  }

  renderRoute(parin, parout);
}

function renderRoute(parin, parout) {
  // render route with parin, parout
  var regex = "";
  var lastIndexIn = parin.length-1;
  for(var i=0; i<lastIndexIn; i++) {
    regex += "/" + parin[i].urlkey + "/{" + parin[i].regexval + ":[^/]*}";
  }

  regexmissingparameter = regex + "/" + parin[lastIndexIn].urlkey;
  regex += "/" + parin[lastIndexIn].urlkey + "/{" + parin[lastIndexIn].regexval + ":[^/]*}";

  var overidden = "";
  parout.forEach(function(item) {
    overidden += '    <overridden-parameter name="' + item + '"></overridden-parameter>\n';
  });

  console.log(
    "  <route>\n" +
    "    <pattern>" + regex + "</pattern>\n" +
         overidden +
    "  </route>");
  console.log(
    "  <route>\n" +
    "    <pattern>" + regexmissingparameter + "</pattern>\n" +
         overidden +
    '    <overridden-parameter name="' + parin[lastIndexIn].urlkey + '"></overridden-parameter>\n' +
    "  </route>");

}

// create combinations
var numcombinations = parameters.length * parameters.length;
var combinationsArray = [];
for(var i=1; i<numcombinations; i++) { // start at 1, we need no empty route
  // generate zero padding
  var zeros = '';
  while (zeros.length < parameters.length) zeros = "0" + zeros;

// binary conversion: 1 -> parameter exists, 0 -> paramerter missing
  var combination = (zeros + i.toString(2)).substr(-parameters.length);
  combinationsArray.push(combination);
}

// sort by number of included parameters
combinationsArray.sort(function(a, b) {
  var paramA = 0;
  var paramB = 0;
  var sumA = 0;
  var sumB = 0;

  for(var i=0; i<a.length; i++) {
    var val = Math.pow(2,(a.length - i));
    if(a.charAt(i) === '1') {
      paramA += 1;
      sumA += val;
    }
    if(b.charAt(i) === '1') {
      paramB += 1;
      sumB += val;
    }
  }

  if(paramA > paramB) {
    return -1;
  }
  if(paramA < paramB) {
    return 1;
  }

  // if both have the same amount of parameters, prefer the first parameters
  if(sumA > sumB) {
    return -1;
  }
  if(sumA < sumB) {
    return 1;
  }

  return 0;
});

// generate route output
combinationsArray.forEach(function(item) {
  prepareArrays(item);
});

console.log(footer);
