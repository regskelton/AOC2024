// Uses the following javascript paradigm:
//
// var name= (function(params) { inner-scoped variables, prototypes, functions })(outerParams);
//
// this creates an object with inner stuff, bound to the params passed in
//
// for an MVC setup, we create a model object, a view object, and use a controller to link them
//

const model = (function () {

        const safetyScore = function (guards) {
            let q1 = 0, q2 = 0, q3 = 0, q4 = 0;

            const yBand = Math.floor(maxY / 2);
            const xBand = Math.floor(maxX / 2);

            for (const g of guards) {
                if (g.py < yBand) {
                    if (g.px < xBand) {
                        q1++;
                    } else {
                        if (g.px > xBand) {
                            q2++;
                        }
                    }
                } else {
                    if (g.py > yBand) {
                        if (g.px < xBand) {
                            q4++;
                        } else {
                            if (g.px > xBand) {
                                q3++;
                            }
                        }
                    }
                }
            }

            return q1 * q2 * q3 * q4;
        }

        const xmasTreeScore = function (guards) {
            let q1 = 0, q2 = 0, q3 = 0, q4 = 0;

            const yBand = Math.floor(maxY / 2);
            const xBand = Math.floor(maxX / 2);
            const band = Math.floor((xBand + yBand) / 2);

            for (const g of guards) {
                if (g.py < yBand) {
                    if (g.px < xBand) {
                        if ((g.px + g.py) > band) {
                            q1++;
                        }
                    } else {
                        if (g.px > xBand) {
                            if ((g.px + g.py - xBand) < band) {
                                q2++;
                            }
                        }
                    }
                } else {
                    if (g.py > yBand) {
                        if (g.px < xBand) {
                            if ((g.px + g.py - yBand) > band) {
                                q4++;
                            }
                        } else {
                            if ((g.px + g.py - (yBand + xBand)) < band) {
                                q3++;
                            }
                        }
                    }
                }
            }
        }

        // 2nd attempt, count all occupied neighbours of all occupied cells
        const xmasTreeScore2 = function (grid) {
            var neighbors = 0;

            for (let y = 1; y < grid.length - 1; y++) {
                for (let x = 1; x < grid[y].length - 1; x++) {

                    if (grid[y][x] > 0) {
                        if (grid[y - 1][x - 1] > 0) neighbors++;
                        if (grid[y - 1][x] > 0) neighbors++;
                        if (grid[y - 1][x + 1] > 0) neighbors++;
                        if (grid[y][x - 1] > 0) neighbors++;
                        if (grid[y][x] > 0) neighbors++;
                        if (grid[y][x + 1] > 0) neighbors++;
                        if (grid[y + 1][x - 1] > 0) neighbors++;
                        if (grid[y + 1][x] > 0) neighbors++;
                        if (grid[y + 1][x + 1] > 0) neighbors++;
                    }
                }

            }

            return neighbors;
        }


        const buildGrid = function () {
            const grid = [];

            for (let i = 0; i < maxY; i++) {
                grid[i] = [];
                for (let j = 0; j < maxX; j++) {
                    grid[i][j] = 0;
                }
            }

            for (const g of guards) {
                grid[g.py][g.px]++;
            }

            return grid;
        }

        const quadrantMask = function () {
            const mask = [];

            for (var x = 0; x < xBand; x++) {
                mask[x] = [];

                for (var y = 0; y < yBand; y++) {
                    mask[x][y] = 1;
                    mask[x][y + yBand] = 1;
                    mask[x + xBand][y] = 1;
                    mask[x + xBand][y + yBand] = 1;
                }
            }

            return mask;
        }

        var guards = [];
        var ticks = 0;
        var maxX;
        var maxY;

        var bestScore = 0;

        const tick = function () {
            ticks++;

            var newGuards = [];

            for (const g of guards) {
                const ng = {
                    px: (((g.px + g.vx) % maxX) + maxX) % maxX,
                    py: (((g.py + g.vy) % maxY) + maxY) % maxY,
                    vx: g.vx,
                    vy: g.vy
                };

                newGuards.push(ng);
            }

            //console.log(`Tick ${ticks}`);
            //for (const g of newGuards) { console.log(`N(${g.px},${g.py})@(${g.vx},${g.vy})`); }

            guards = Array.from(newGuards);

            const grid = buildGrid();

            var score = xmasTreeScore2(grid);

            if (score > bestScore) {
                view.drawPixels(grid);

                bestScore = score;

                console.log(`Tick ${ticks}, best xmasTreeScore=${score}`);
            }
        }

        return {
            init: function (filename, _maxX, _maxY, doneCallback) {
                maxX = _maxX;
                maxY = _maxY;

                var xmlhttp = new XMLHttpRequest();

                xmlhttp.onreadystatechange = function () {
                    if (xmlhttp.status == 200 && xmlhttp.readyState == 4) {

                        var lines = xmlhttp.responseText.split(/\r?\n/);

                        for (const line of lines) {
                            //p=0,4 v=3,-3
                            var groups = line.match(/p=(-?\d+),(-?\d+) v=(-?\d+),(-?\d+)/);

                            var px = parseInt(groups[1]);
                            var py = parseInt(groups[2]);
                            var vx = parseInt(groups[3]);
                            var vy = parseInt(groups[4]);

                            guards.push({px: px, py: py, vx: vx, vy: vy});
                        }

                        //for (const g of guards) { console.log(`(${g.px},${g.py})@(${g.vx},${g.vy})`); }

                        setInterval(tick, 1);
                    }

                    doneCallback();
                }

                xmlhttp.open("GET", filename, true);
                xmlhttp.send();
            },
        }
    }
)
();

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

        drawPixels: function (grid) {
            var rows = "";

            var scale = {
                x: canvas.width / grid[0].length,
                y: canvas.height / grid.length
            }

            this.clearScreen();

            for (let y = 0; y < grid.length; y++) {
                for (let x = 0; x < grid[y].length; x++) {

                    context.beginPath();
                    context.arc(x * scale.x, y * scale.y, grid[y][x], 0, 2 * Math.PI, false);
                    context.fillStyle = '#003300';
                    context.fill();
                    context.lineWidth = 1;
                    context.setLineDash([]);
                    context.strokeStyle = '#003300';
                    context.stroke();
                }
            }
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

const controller = (function (m, v) {

        const modelInitDone = function () {
        }

        return {

            init: function (filename, maxX, maxY) {

                model.init(filename, maxX, maxY, modelInitDone);

            }
        };
    }
)
(model, view);

//controller.init("example.txt", 11, 7);
controller.init("input.txt", 101, 103);