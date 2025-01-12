// Uses the following javascript paradigm:
//
// var name= (function(params) { inner-scoped variables, prototypes, functions })(outerParams);
//
// this creates an object with inner stuff, bound to the params passed in
//
// for an MVC setup, we create a model object, a view object, and use a controller to link them
//

var model = (function () {
    var nDims, scale, bigG, things = [];

    var Thing = function (name, initialPosition, initialVelocity, mass, size, color) {
        this.name = name;
        this.position = initialPosition;
        this.velocity = initialVelocity;
        this.mass = mass;
        this.size = size;
        this.color = color;
        this.acceleration = [0, 0];
    }

    Thing.prototype.move = function () {
        for (d = 0; d < nDims; d++) {
            this.velocity[d] += this.acceleration[d];

            this.position[d] += this.velocity[d];
        }
    }

    Thing.prototype.bounce = function () {
        for (i = 0; i < nDims; i++) {
            if (this.position[i] < 0) {
                this.position[i] = 0;
                this.velocity[i] = -this.velocity[i];
            }

            if (this.position[i] > scale) {
                this.position[i] = scale;
                this.velocity[i] = -this.velocity[i];
            }
        }
    }

    Thing.prototype.wrap = function () {
        for (i = 0; i < nDims; i++) {
            if (this.position[i] < 0) {
                this.position[i] = scale;
            }

            if (this.position[i] > scale) {
                this.position[i] = 0;
            }
        }
    }

    var toPolar = function (inputVector) {
        var sumOfSquares = 0;

        for (d = 0; d < inputVector.length; d++) {
            sumOfSquares += inputVector[d] * inputVector[d];
        }

        //todo: generalise angle - not easy!
        return {
            scalar: Math.sqrt(sumOfSquares),
            angle: Math.atan(inputVector[1] / inputVector[0])
        };
    }

    //var maxX, maxY;

    return {
        init: function (filename, maxX, maxY) {
            var grid = Array.from(Array(maxX), () => new Array(maxY));

            console.log("Grid is in " + filename);

            console.log("Grid= ", grid);

            for (var r = 0; r < grid.length; r++) {
                for (var c = 0; c < grid[r].length; c++) {
                    grid[r][c] = '.';
                }
            }


            var xmlhttp = new XMLHttpRequest();
            xmlhttp.onreadystatechange = function () {
                if (xmlhttp.status == 200 && xmlhttp.readyState == 4) {
                    var lines = xmlhttp.responseText.split(/\r?\n/);

                    for (const line of lines) {
                        var coords = line.split(',');
                        var x = parseInt(coords[0]);
                        var y = parseInt(coords[1]);

                        console.log("Hash at " + x + "," + y);

                        grid[x - 1][y - 1] = '#';
                    }

                    console.log("Grid= ", grid);

                    for (var r = 0; r < grid.length; r++) {
                        for (var c = 0; c < grid[r].length; c++) {
                            if (grid[r][c] === '#') {
                                console.log("Hash found at " + r + "," + c);
                            }
                        }
                    }

                    // alert(coords);
                }
            }
            xmlhttp.open("GET", filename, true);
            xmlhttp.send();


        },

        createSomething: function (name, position, velocity, mass, size, color) {
            things.push(new Thing(name, position, velocity, mass, size, color));
        },

        moveAll: function () {
            things.forEach(function (thing) {
                thing.move();
            });
        },

        bounceAll: function () {
            things.forEach(function (thing) {
                thing.bounce();
            });
        },

        wrapAll: function () {
            things.forEach(function (thing) {
                thing.wrap();
            });
        },

        collide: function () {
            for (i = 0; i < things.length; i++) {
                for (j = i; j < things.length; j++) {
                    if (i !== j) {

                        var rSquared = 0;

                        for (d = 0; d < nDims; d++) {
                            rSquared += (things[i].position[d] - things[j].position[d]) * (things[i].position[d] - things[j].position[d])
                        }

                        var minDistance = (things[i].size + things[j].size) * (things[i].size + things[j].size);

                        if (rSquared < minDistance) {
                            console.log("Smash ", things[i], things[j]);

                            var t = things[i].velocity;
                            things[i].velocity = things[j].velocity;
                            things[j].velocity = t;
                        }
                    }
                }
            }
        },

        applyGravity: function () {

            for (i = 0; i < things.length; i++) {
                // Clear existing acceleration vectors & potentials
                for (d = 0; d < nDims; d++) {
                    things[i].acceleration[d] = 0;
                }

                things[i].potentialEnergy = 0;
            }

            //apply gravity
            for (i = 0; i < things.length; i++) {

                // Add acceleration due to each other object
                for (j = i + 1; j < things.length; j++) {
                    if (i !== j) { //no self acceleration
                        var rSquared = 0;

                        // Pythagorean / Euclidean distance is the root of the sum of
                        // the squares of distances in each dimension
                        for (d = 0; d < nDims; d++) {
                            rSquared += (things[j].position[d] - things[i].position[d]) * (things[j].position[d] - things[i].position[d])
                        }

                        var r = Math.sqrt(rSquared);

                        // gravitational potential = - (G x Mi x Mj) / r
                        var gravitationalPotential = -bigG * things[i].mass * things[j].mass / r;

                        things[i].potentialEnergy += gravitationalPotential;

                        // gravitationalForce = (G x Mi x Mj) / r^2
                        var gravitationalForce = bigG * things[i].mass * things[j].mass / rSquared;

                        //apply accelerationScalar projected to each dimension
                        //...and mutually opposite
                        for (d = 0; d < nDims; d++) {
                            things[i].acceleration[d] += gravitationalForce * ((things[j].position[d] - things[i].position[d]) / r) / things[i].mass;

                            things[j].acceleration[d] -= gravitationalForce * ((things[j].position[d] - things[i].position[d]) / r) / things[j].mass;
                        }
                    }
                }
            }
        },

        getMetrics: function () {
            var momentumArray = []; // momentum is a vector
            var kinetic = 0;
            var potential = 0;

            for (d = 0; d < nDims; d++) {
                momentumArray[d] = 0;
            }

            for (i = 0; i < things.length; i++) {
                for (d = 0; d < nDims; d++) {
                    momentumArray[d] += things[i].mass * things[i].velocity[d];

                    kinetic += (things[i].mass * things[i].velocity[d] * things[i].velocity[d]) / 2;
                }

                potential += things[i].potentialEnergy;
            }

            return {
                momentum: toPolar(momentumArray),
                kineticEnergy: kinetic,
                potentialEnery: potential
            };
        },

        getThings: function () {
            return things;
        }
    }
})();

var view = function () {
    var canvas = document.getElementById('canvas');

    var context = canvas.getContext('2d');

    var DOM = {
        grid: document.querySelector('.grid_display')
    };

    return {
        init: function () {
            console.log('view init');
        },

        clearScreen: function () {
            context.clearRect(0, 0, canvas.width, canvas.height);
        },

        draw: function (grid) {
            var rows = "";

            for (const row of grid) {
                rows += '<tr>'
                for (const cell of row) {

                    if (cell == 0) {
                        ch = '.';
                    } else {
                        ch = cell;
                    }

                    rows += `<td>${ch}</td>`
                }
                rows += '</tr>'
            }

            DOM.grid.innerHTML = rows;
        }
    }
}();

var controller = (function (m, v) {

        class Rule {
            constructor(inputLeft, operator, inputRight, output) {
                this._inputLeft = inputLeft;
                this._operator = operator;
                this._inputRight = inputRight;
                this._output = output;
            }

            set inputLeft(newValue) {
                this._inputLeft = newValue
            }

            set inputRight(newValue) {
                this._inputRight = newValue
            }

            set operator(newValue) {
                this._operator = newValue
            }

            set output(newValue) {
                this._output = newValue
            }

            get inputLeft() {
                return this._inputLeft;
            }

            get operator() {
                return this._operator;
            }

            get inputRight() {
                return this._inputRight;
            }

            get output() {
                return this._output;
            }

            toString = function () {
                return `${this._output}=${this._operator}(${this._inputLeft},${this._inputRight})`
            }
        }

        var outputs = new Map();

        function evaluate(target, inputs, rules) {
            var result;

            if (inputs.has(target)) {
                result = inputs.get(target);

//                console.log(`DI: ${target} => ${result ? 1 : 0}`);
            } else {
                if (outputs.has(target)) {
                    result = outputs.get(target);

//                    console.log(`DO: ${target} => ${result ? 1 : 0}`);
                } else {
                    if (!rules.has(target)) {
                        console.log(`eh? ${target}`);
                    }

                    var rule = rules.get(target);

                    var left = evaluate(rule.inputLeft, inputs, rules);
                    var right = evaluate(rule.inputRight, inputs, rules);
                    switch (rule.operator) {
                        case "AND" :
                            result = left && right;
                            break;
                        case "OR" :
                            result = left || right;
                            break;
                        case "XOR" :
                            result = left !== right;
                            break;
                    }

//                    console.log(`Stored ${target} as ${result ? 1 : 0}`);
                    outputs.set(target, result ? 1 : 0);

//                    console.log(`${rule}: ${rule.operator}(${left},${right}) => ${result ? 1 : 0}`);
                }
            }

            return result ? 1 : 0;
        }

        function fromBinary(rules, inputs, results, match) {
            for (const [key, rule] of rules) {
//                            console.log(`Eval ${rule}?`);

                if (rule.output.startsWith(match)) {
                    i = parseInt(rule.output.substring(1));

                    results[i] = evaluate(rule.output, inputs, rules);
                }
            }

            var total = 0;
            for (var i = results.length - 1; i >= 0; i--) {
//                            console.log(`z[${i}]=${z[i]}`);

                total = total * 2 + results[i];
            }
            return total;
        }

        return {
            init: function (filename) {

                var xmlhttp = new XMLHttpRequest();


                xmlhttp.onreadystatechange = function () {
                    if (xmlhttp.status == 200 && xmlhttp.readyState == 4) {

                        var lines = xmlhttp.responseText.split(/\r?\n/);

                        var firstPart = true;

                        var inputs = new Map();
                        var rules = new Map();

                        for (const line of lines) {
                            var groups = [];

                            if (firstPart) {
                                if (line.length === 0) {
                                    firstPart = false;
                                } else {
                                    groups = line.match(/(\w+): ([01])/);

                                    inputs.set(groups[1], parseInt(groups[2]));
                                }
                            } else {
                                groups = line.match(/(\w+) (AND|OR|XOR) (\w+) -> (\w+)/);

                                rules.set(groups[4], new Rule(groups[1], groups[2], groups[3], groups[4]));
                            }
                        }

                        // for (const [k, v] of inputs) {
                        //     console.log(`${k}=>${v}`);
                        // }
                        // for (const i of rules) {
                        //     console.log(`${i}`);
                        // }


                        // "evaluate" all the z's
                        var x = [];
                        var y = [];
                        var z = [];

                        var totalX = fromBinary(rules, inputs, x, "x");
                        var totalY = fromBinary(rules, inputs, y, "y");
                        var totalZ = fromBinary(rules, inputs, z, "z");

                        console.log(`${totalX}`);
                        console.log(`${totalY}`);
                        console.log(`${totalZ}`);
                    }
                }
                xmlhttp.open("GET", filename, true);
                xmlhttp.send();
            }
        };
    }
)
(model, view);

controller.init("input.txt");