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

        function fits(key, lock) {
            var result = true;

            for (var t = 0; t < 5; t++) {
                if ((key.tumblers[t] + lock.tumblers[t]) > 5) {
                    result = false;
                }
            }

            console.log(`Key ${key.tumblers[0]}${key.tumblers[1]}${key.tumblers[2]}${key.tumblers[3]}${key.tumblers[4]}`);
            console.log(`Lock ${lock.tumblers[0]}${lock.tumblers[1]}${lock.tumblers[2]}${lock.tumblers[3]}${lock.tumblers[4]}`);
            console.log( `${result}`);

            return result;
        }

        return {
            init: function (filename) {

                var xmlhttp = new XMLHttpRequest();


                xmlhttp.onreadystatechange = function () {
                    if (xmlhttp.status == 200 && xmlhttp.readyState == 4) {

                        var lines = xmlhttp.responseText.split(/\r?\n/);

                        var keys = [];
                        var locks = [];

                        for (var l = 0; l < lines.length; l = l + 8) {
                            var tumblers = [0, 0, 0, 0, 0];

                            for (var h = 1; h < 6; h++) {
                                for (var t = 0; t < 5; t++) {
                                    if (lines[l + h][t] === "#") {
                                        tumblers[t]++;
                                    }
                                }
                            }
                            if (lines[l] === "#####") {
                                locks.push({tumblers});
                            } else {
                                keys.push({tumblers});
                            }
                        }

                        for (const l of locks) {
                            console.log(`Lock ${l.tumblers[0]}${l.tumblers[1]}${l.tumblers[2]}${l.tumblers[3]}${l.tumblers[4]}`);
                        }
                        for (const l of keys) {
                            console.log(`Key  ${l.tumblers[0]}${l.tumblers[1]}${l.tumblers[2]}${l.tumblers[3]}${l.tumblers[4]}`);
                        }

                        var fitted = 0;

                        for (const k of keys) {
                            for (const l of locks) {
                                if( fits(k,l)) {
                                    fitted++;
                                } else {

                                }
                                console.log(`Lock ${l.tumblers[0]}${l.tumblers[1]}${l.tumblers[2]}${l.tumblers[3]}${l.tumblers[4]}`);
                            }
                        }

                        console.log(`${fitted}`);

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