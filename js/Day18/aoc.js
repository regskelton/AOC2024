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

                        grid[x-1][y-1] = '#';
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

var view = (function () {
    var canvas = document.getElementById('canvas');

    var context = canvas.getContext('2d');

    var scale;
    var useLoggedScale = true;

    var DOM = {
        dimensions: document.querySelector('.info__dimensions--value'),
        momentum: document.querySelector('.info__momentum--value'),
        time: document.querySelector('.info__time--value'),
    };

    var velocityScale = Math.pow(10, -3);
    var accelerationScale = 2.5 * Math.pow(10, 3);

    function formatTime(time) {
        const seconds_per_year = 365 * 24 * 60 * 60;
        const seconds_per_day = 24 * 60 * 60;
        const seconds_per_hour = 60 * 60;
        const seconds_per_minute = 60;

        var years = Math.floor(time / seconds_per_year);
        time = time - years * seconds_per_year;

        var days = Math.floor(time / seconds_per_day);
        time = time - days * seconds_per_day;

        var hours = Math.floor(time / seconds_per_hour);
        time = time - hours * seconds_per_hour;

        var mins = Math.floor(time / seconds_per_minute);
        time = time - mins * seconds_per_minute;

        return `${years} years, ${days} days, ${hours}:${mins}:${time}`;
    }

    return {
        init: function (paramScales) {
            console.log('view init');

            scale = paramScales;

            //var img = document.getElementById("background");

            //context.drawImage(img, 0, 0);

            var tests = [
                [3, 4],
                [3, -4],
                [-3, 4],
                [-3, -4]
            ];
            tests.forEach(ele => {
                console.log(ele);
            });
        },

        clearScreen: function () {
            context.clearRect(0, 0, canvas.width, canvas.height);
        },

        draw: function (grid) {

//            //console.log(color);
//
//            var myScale= scale;
//
//            if(useLoggedScale)
//            {
//                var polarCentre= toPolar(position);
//
//                position= fromPolar( Math.log10( polarCentre.scalar), polarCentre.angle);
//
//                myScale= 2 * Math.log10(scale);
//            }
//
//            var centre = {
//                x: ((position[0] + myScale / 2) / myScale) * canvas.width,
//                y: canvas.height - ((position[1] + myScale / 2) / myScale) * canvas.height
//            }
//
//            var velocityVector = {
//                x: centre.x + velocity[0] * velocityScale,
//                y: centre.y - velocity[1] * velocityScale
//            }
//
//            var accelerationVector = {
//                x: centre.x + acceleration[0] * accelerationScale,
//                y: centre.y - acceleration[1] * accelerationScale
//            }
//
//            //draw velocity and the acceleration vectors
//            context.beginPath();
//            context.lineWidth = 1;
//            context.moveTo(centre.x, centre.y);
//            context.setLineDash([]);
//            context.lineTo(velocityVector.x, velocityVector.y);
//            context.stroke();
//
//            context.beginPath();
//            context.lineWidth = 1;
//            context.moveTo(centre.x, centre.y);
//            context.setLineDash([2, 1]);
//            context.lineTo(accelerationVector.x, accelerationVector.y);
//            context.stroke();
//
//            context.beginPath();
//            context.arc( centre.x, centre.y, size, 0, 2 * Math.PI, false);
//            context.fillStyle = color;
//            context.fill();
//            context.lineWidth = 1;
//            context.setLineDash([]);
//            context.strokeStyle = '#003300';
//            context.stroke();
        },

        updateText: function (nDims, momentumPolar, time) {
            DOM.dimensions.innerHTML = nDims;
            DOM.momentum.innerHTML = formatLargeNumber(momentumPolar.scalar, 12) + ' @ ' + Math.round(momentumPolar.angle * 180 / Math.PI) + '&deg;';
            DOM.time.innerHTML = formatTime(time);
        },

    }

})();

var controller = (function (m, v) {

    return {
        init: function () {
            console.log('controller init');

            m.init("example.txt", 6, 6);

            v.init(5);

            console.log('back');


            console.log("Jump to hyperspace...");
        }
    };
})(model, view);

controller.init();