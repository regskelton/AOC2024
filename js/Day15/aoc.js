// Uses the following javascript paradigm:
//
// var name= (function(params) { inner-scoped variables, prototypes, functions })(outerParams);
//
// this creates an object with inner stuff, bound to the params passed in
//
// for an MVC setup, we create a model object, a view object, and use a controller to link them
// Uses the following javascript paradigm:
//
// var name= (function(params) { inner-scoped variables, prototypes, functions })(outerParams);
//
// this creates an object with inner stuff, bound to the params passed in
//
// for an MVC setup, we create a model object, a view object, and use a controller to link them
const view = function () {

    const DOM = {
        grid: document.querySelector('.charGrid'),
        info: document.querySelector('.infoGrid')
    };

    const escapeHtml = (unsafe) => {
        return unsafe.replaceAll('&', '&amp;').replaceAll('<', '&lt;').replaceAll('>', '&gt;').replaceAll('"', '&quot;').replaceAll("'", '&#039;');
    }

    return {
        init: function () {
            console.log('view init');
        },

        initInfo: function (infos) {
            var rows = "";

            var id = 0;

            for (const info of infos) {
                rows += `<tr><td><div class="info_label_${id}">${info.label}</div></td><td><div class="info_value_${id}">${info.value}</div></td></tr>`

                id++;
            }

            DOM.info.innerHTML = rows;
        },

        drawInfo: function (id, value) {
            document.querySelector(`.info_value_${id}`).innerHTML = escapeHtml("" + value);

            //console.log(`${id} <= ${value}`);
        },

        draw: function (grid) {
            let rows = "";
            let text="";

            for (const row of grid) {
                rows += '<tr>'
                for (const cell of row) {
                    rows += `<td>${cell}</td>`
                    text+=`${cell}`;
                }
                rows += '</tr>'
                text+="\n";
            }

            DOM.grid.innerHTML = rows;

            console.log( text);
        }
    }
}();

const model = (function () {

        let ticks = 0;
        let board;
        let moves;
        let robot;

        function setCharAt(str,index,chr) {
            if(index > str.length-1) return str;
            return str.substring(0,index) + chr + str.substring(index+1);
        }

        function findSpace(board, robot, delta) {
            let space= {r:robot.r+delta.r, c:robot.c+delta.c};

            while( board[space.r][space.c] === 'O' && board[space.r][space.c] !== '#') {
                space= {r:space.r+delta.r, c:space.c+delta.c};
            }

            if( board[space.r][space.c] === '#')
                return null;

            //console.log(`Space at (${space.r},${space.c})`);

            return space;
        }

        function score( board) {
            let gps=0;

            for( let r=0; r < board.length; r++) {
                for( let c=0; c < board[r].length; c++) {
                    if( board[r][c]==='O') {
                        gps= gps + r * 100 + c;

                        //console.log(`Box at (${r},${c}), add ${r*100+c}, total ${gps}`);
                    }
                }

            }

            return gps;
        }

        return {
            init: function (filename, doneCallback) {

                const xmlhttp = new XMLHttpRequest();

                xmlhttp.onreadystatechange = function () {
                    let firstPart = true; //blank line separates second part

                    if (xmlhttp.status === 200 && xmlhttp.readyState === 4) {

                        const lines = xmlhttp.responseText.split(/\r?\n/);

                        board = [];

                        for (const line of lines) {
                            if (firstPart) {
                                if (line.length > 1) {
                                    board.push(line);
                                } else {
                                    firstPart = false;

                                    moves = "";
                                }
                            } else {
                                moves += line;
                            }
                        }

                        //find robot
                        for (let row = 0; row < board.length; row++) {
                            for (let col = 0; col < board[0].length; col++) {
                                if (board[row][col] === "@") {
                                    robot = {r: row, c: col};
                                }
                            }
                        }

                        view.draw(board);

                        const infos = [
                            {id: 0, label: "Ticks", value: "0"},
                            {id: 1, label: "Robot", value: ` (${robot.r},${robot.c})`},
                            {id: 2, label: "Moves", value: " *" + moves + "*" + moves.length},
                        ];

                        view.initInfo(infos);
                    }

                    doneCallback();
                }

                xmlhttp.open("GET", filename, true);
                xmlhttp.send();
            },

            tick: function () {
                if (ticks < moves.length) {
                    const delta = {r:0,c:0};

                    switch (moves[ticks]) {
                        case '<':
                            delta.c--;

                            break;
                        case '>':
                            delta.c++;

                            break;
                        case '^':
                            delta.r--;

                            break;
                        case 'v':
                            delta.r++;

                            break;
                        default:
                            console.log(`Unknown move ${moves[ticks]}`);

                            break;
                    }

                    switch (board[robot.r+delta.r][robot.c+delta.c]) {
                        case '#':
                            //console.log("Bump!");
                            //no can do
                            break;

                        case '.':
                            //console.log(`Woo hoo: (${robot.r},${robot.c}) -> (${delta.r},${delta.c})`);
                            //direct move
                            board[robot.r + delta.r]= setCharAt( board[robot.r + delta.r], robot.c + delta.c, '@');
                            board[robot.r]= setCharAt( board[robot.r], robot.c, '.');

                            robot = {r: robot.r+delta.r, c: (robot.c + delta.c)};

                            break;

                        case 'O':
                            //console.log("Box");
                            const space = findSpace(board, robot, delta);

                            if( space != null) {
                                for(let r= space.r, c=space.c; (r !== robot.r) || (c !== robot.c); r-=delta.r, c-=delta.c) {
                                    //console.log( `Copying (${r-delta.r},${c-delta.c}) to (${r},${c})`);
                                    board[r]= setCharAt( board[r], c, board[r-delta.r][c-delta.c]);
                                }

                                board[robot.r]= setCharAt( board[robot.r], robot.c, '.');

                                robot = {r: robot.r+delta.r, c: (robot.c + delta.c)};
                            }

                            break;
                        default:
                            console.log( `Unknown cell(${robot.r+delta.r},${robot.c+delta.c})=${board[robot.r+delta.r][robot.c+delta.c]}`);
                    }

                    //view.draw(board);
                    view.drawInfo(0, ticks+1);
                    //view.drawInfo(1, ` (${robot.r},${robot.c})`);

                    ticks++;

                    if (ticks === moves.length) {
                        view.draw(board);
                        view.drawInfo(0, ticks+1);
                        view.drawInfo(1, ` (${robot.r},${robot.c})`);

                        console.log( `GPS= ${score(board)}`);
                    }
                }
            }

        }
    }
)
();

const controller = (function (m, v) {

        const modelInitDone = function () {
            setInterval(model.tick, 1);
        }

        return {
            init: function (filename) {
                model.init(filename, modelInitDone);
            }
        };
    }
)
(model, view);

//controller.init("example.txt");
//controller.init("example2.txt");
controller.init("input.txt");