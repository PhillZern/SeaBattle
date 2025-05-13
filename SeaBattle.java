import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

class TestSeaBattle {
    public static void main(String[] args) throws InterruptedException {
        SeaBattle seaBattle = new SeaBattle();
        seaBattle.play();
    }
}

public class SeaBattle {
    private final Player player1;
    private final Player player2;
    private boolean isPlaying = false;
    private static final Scanner scanner = new Scanner(System.in);
    public SeaBattle() {
        player1 = new Player();
        player2 = new Player();
    }
    public void play() throws InterruptedException {
        System.out.println("                     ☠️ Морской бой! ⚔️");
        player1.showAll();
        player1.playerWait();
        isPlaying = true;
        player1.place();
        player2.place();
        while (isPlaying) {
            player1.guesses(player2);
            player2.guesses(player1);
        }
    }
    private class Player {
        private int id = 0;
        private static int playerCounter = 0;
        private final String[][] field;
        private final String[][] otherField;
        private final int[][] fieldWithShips;
        private final List<Integer> shipsWounded;
        private final String emptyCell = "\033[1;37m▨\033[0m";
        private final String shipCell = "\033[1;32m▨\033[0m";
        private final String shipCellInjured = "\033[1;38;5;208m▨\033[0m";
        private final String shipCellKilled = "\033[1;31m▨\033[0m";
        private final String shipCellMissed = "\033[30m▦\033[0m";
        private int fourCellSheep = 1;
        private int threeCellSheep = 1;
        private int twoCellSheep = 1;
        private int oneCellSheep = 0;
        private int totalPlayerShips = fourCellSheep + threeCellSheep + twoCellSheep + oneCellSheep;
        public Player() {
            playerCounter++;
            this.id += playerCounter;
            this.field = new String[10][10];
            this.otherField = new String[10][10];
            this.fieldWithShips = new int[10][10];
            this.shipsWounded = new ArrayList<>(totalPlayerShips);
            for (int i = 0; i < 10; i++) {
                for (int j = 0; j < 10; j++) {
                    field[i][j] = emptyCell;
                    otherField[i][j] = emptyCell;
                }
            }

        }
        private void playerWait() throws InterruptedException {
            Thread.sleep(3000);
            for (int i = 0; i < 30; i++) {
                Thread.sleep(200);
                System.out.print("\n");
            }
        }
        private void show() {
            System.out.println("  A   Б  В  Г   Д  Е  Ж   З  И  К");
            for (int i = 0; i < 10; i++) {
                System.out.print(i + " ");
                for (int j = 0; j < 10; j++) {
                    System.out.print(field[i][j] + " ");
                }
                System.out.println();
            }
        }
        public void showAll() {
            if (isPlaying) {
                System.out.println("         Ваше поле                    Поле соперника");
            } else {
                System.out.println("       Поле 1 игрока                    Поле 2 игрока");
            }
            System.out.println("  A   Б  В  Г   Д  Е  Ж   З  И  К         A   Б  В  Г   Д  Е  Ж   З  И  К");
            for (int i = 0; i < 10; i++) {
                System.out.print(i + " ");
                for (int j = 0; j < 10; j++) {
                    System.out.print(field[i][j] + " ");
                }
                System.out.print("  │  " + i + " ");
                for (int j = 0; j < 10; j++) {
                    System.out.print(otherField[i][j] + " ");
                }
                System.out.println();
            }
        }

        private void guesses(Player otherPlayer) throws InterruptedException {
            if (isPlaying) {
                System.out.println("► Игрок-" + id + " ходит:");
                showAll();
                System.out.println("Введите клетку:");
                String choice = scanner.nextLine();
                while (!choice.matches("[а-к][0-9]")) {
                    System.out.println("Такой клетки не существует!\n► Игрок-" + id + " ходит:");
                    showAll();
                    System.out.println("Введите клетку:");
                    choice = scanner.nextLine();
                }
                int horizontal = getHorizontal(choice.toUpperCase().charAt(0));
                int vertical = Character.getNumericValue(choice.charAt(1));
                if (otherPlayer.field[vertical][horizontal].equals(shipCell)) {
                    otherPlayer.field[vertical][horizontal] = otherField[vertical][horizontal] = shipCellInjured;
                    System.out.println("Попадание! 💣💥");
                    boolean isNewWounded = true;
                    for (Integer ship : otherPlayer.shipsWounded) {
                        if (otherPlayer.fieldWithShips[vertical][horizontal] == ship) {
                            isNewWounded = false;
                            break;
                        }
                    }
                    if (isNewWounded) {
                        otherPlayer.shipsWounded.add(otherPlayer.fieldWithShips[vertical][horizontal]);
                    }
                    otherPlayer.fieldWithShips[vertical][horizontal] -= 11;
                    boolean isKilled = true;
                    int shipToRemove = 0;
                    for (Integer ship : otherPlayer.shipsWounded) {
                        for (int i = 0; i < 10; i++) {
                            for (int j = 0; j < 10; j++) {
                                if (otherPlayer.fieldWithShips[i][j] == ship) {
                                    isKilled = false;
                                    continue;
                                }
                            }
                        }
                        if (isKilled) {
                            System.out.println("🏴‍☠️ Корабль потоплен! 🏴‍☠️");
                            shipToRemove = ship;
                            break;
                        }
                    }
                    if (isKilled) {
                        for (int i = 0; i < otherPlayer.shipsWounded.size(); i++) {
                            if (otherPlayer.shipsWounded.get(i) == shipToRemove) {
                                otherPlayer.shipsWounded.remove(i);
                            }
                        }
                        for (int i = 0; i < 10; i++) {
                            for (int j = 0; j < 10; j++) {
                                if (otherPlayer.fieldWithShips[i][j] == shipToRemove-11) {
                                    otherPlayer.field[i][j] = shipCellKilled;
                                    otherField[i][j] = shipCellKilled;
                                }
                            }
                        }
                    }
                    checkWin(otherPlayer);
                    guesses(otherPlayer);
                } else if (otherPlayer.field[vertical][horizontal].equals(shipCellKilled) || otherPlayer.field[vertical][horizontal].equals(shipCellInjured) || otherPlayer.field[vertical][horizontal].equals(shipCellMissed)) {
                    System.out.println("Эта клетка уже отмечена!");
                    guesses(otherPlayer);
                } else {
                    otherPlayer.field[vertical][horizontal] = otherField[vertical][horizontal] = shipCellMissed;
                    System.out.println("Мимо! 💣💦");
                    showAll();
                    playerWait();
                }
            }
        }
        private void checkWin(Player otherPlayer) {
            isPlaying = false;
            for (int i = 0; i < 10; i++) {
                for (int j = 0; j < 10; j++) {
                    if (otherPlayer.field[i][j].equals(shipCell)) {
                        isPlaying = true;
                        break;
                    }
                }
            }
            if (!isPlaying) {
                System.out.println("\n\n\n\n\n\n\n\n\n");
                showAll();
                System.out.println("\uD83C\uDF89 Player-" + this.id + " win! \uD83C\uDF89");
            }
        }
        private void place() throws InterruptedException {
            while (totalPlayerShips != 0) {
                System.out.println("► Игрок-" + this.id + " ставит корабли:");
                show();
                System.out.println("Осталось разместить: " + totalPlayerShips + " кораблей\nРазместить:\n1) " + oneCellSheep + " - однопалубных\n2) " + twoCellSheep + " - двухпалубных\n3) " + threeCellSheep + " - трёхпалубных\n4) " + fourCellSheep + " - четырёхпалубных");
                String choice = scanner.nextLine();
                while (!choice.matches("[1-4]")) {
                    System.out.println("Такого корабля не существует!\n► Игрок-" + this.id + " ставит корабли:");
                    show();
                    System.out.println("Разместить:\n1) " + oneCellSheep + " - однопалубных\n2) " + twoCellSheep + " - двухпалубных\n3) " + threeCellSheep + " - трёхпалубных\n4) " + fourCellSheep + " - четырёхпалубных");
                    choice = scanner.nextLine();
                }
                int choice1 = Integer.parseInt(choice);
                if ((choice1 == 1 && oneCellSheep == 0) || (choice1 == 2 && twoCellSheep == 0) || (choice1 == 3 && threeCellSheep == 0) || (choice1 == 4 && fourCellSheep == 0)) {
                    System.out.println("У вас не осталось " + choice + "-палубных кораблей");
                    continue;
                }
                System.out.println("Куда разместить " + choice1 + "-палубный корабль? ");
                choice = scanner.nextLine();
                while (!choice.matches("[а-к][0-9]")) {
                    System.out.println("Такой клетки не существует!\n► Игрок-" + this.id + " ставит корабли:");
                    show();
                    System.out.println("Куда разместить " + choice1 + "-палубный корабль? ");
                    choice = scanner.nextLine();
                }
                int horizontal = getHorizontal(choice.toUpperCase().charAt(0));
                int vertical = Character.getNumericValue(choice.charAt(1));
//                System.out.println("Horizontal: " + horizontal + " Vertical: " + vertical + " ");
                boolean isOtherShipsNotNear = true;
                boolean isBorderNotNear = true;
                if (choice1 > 1 && choice1 < 5) {
                    System.out.println("Куда ведём корму?\n1) Вверх\n2) Вниз\n3) Вправо\n4) Влево");
                    choice = scanner.nextLine();
                    while (!choice.matches("[1-4]")) {
                        System.out.println("Такого направления нет!\n► Игрок-" + this.id + " ставит корабли:");
                        show();
                        System.out.println("Куда ведём корму?\n1) Вверх\n2) Вниз\n3) Вправо\n4) Влево");
                        choice = scanner.nextLine();
                    }
                    switch (Integer.parseInt(choice)) {
                        case 1:
                            // Проверка на near других кораблей
                            for (int i = -1; i < choice1+1; i++) {
                                for (int j = -1; j < 2; j++) {
                                    try {
                                        if (field[vertical-i][horizontal+j].equals(shipCell)) {
                                            isOtherShipsNotNear = false;
                                        }
                                    } catch (ArrayIndexOutOfBoundsException e) {
                                        continue;
                                    }
                                }
                            }
                            // Проверка на границы
                            try {
                                for (int i = 0; i < choice1; i++) {
                                    field[vertical-i][horizontal].isEmpty();
                                }
                            } catch (ArrayIndexOutOfBoundsException e) {
                                isBorderNotNear = false;
                            }
                            if (isBorderNotNear && isOtherShipsNotNear) {
                                for (int i = 0; i < choice1; i++) {
                                    field[vertical-i][horizontal] = shipCell;
                                    fieldWithShips[vertical-i][horizontal] = totalPlayerShips;
                                }
                            } else {
                                System.out.println("Здесь ставить корабль нельзя!");
                            }
                            break;
                        case 2:
                            // Проверка на near других кораблей
                            for (int i = -1; i < choice1+1; i++) {
                                for (int j = -1; j < 2; j++) {
                                    try {
                                        if (field[vertical+i][horizontal+j].equals(shipCell)) {
                                            isOtherShipsNotNear = false;
                                        }
                                    } catch (ArrayIndexOutOfBoundsException e) {
                                        continue;
                                    }
                                }
                            }
                            // Проверка на границы
                            try {
                                for (int i = 0; i < choice1; i++) {
                                    field[vertical+i][horizontal].isEmpty();
                                }
                            } catch (ArrayIndexOutOfBoundsException e) {
                                isBorderNotNear = false;
                            }
                            if (isBorderNotNear && isOtherShipsNotNear) {
                                for (int i = 0; i < choice1; i++) {
                                    field[vertical+i][horizontal] = shipCell;
                                    fieldWithShips[vertical+i][horizontal] = totalPlayerShips;
                                }
                            } else {
                                System.out.println("Здесь ставить корабль нельзя!");
                            }
                            break;
                        case 3:
                            // Проверка на near других кораблей
                            for (int i = -1; i < 2; i++) {
                                for (int j = -1; j < choice1+1; j++) {
                                    try {
                                        if (field[vertical-i][horizontal+j].equals(shipCell)) {
                                            isOtherShipsNotNear = false;
                                        }
                                    } catch (ArrayIndexOutOfBoundsException e) {
                                        continue;
                                    }
                                }
                            }
                            // Проверка на границы
                            try {
                                for (int i = 0; i < choice1; i++) {
                                    field[vertical][horizontal+i].isEmpty();
                                }
                            } catch (ArrayIndexOutOfBoundsException e) {
                                isBorderNotNear = false;
                            }
                            if (isBorderNotNear && isOtherShipsNotNear) {
                                for (int i = 0; i < choice1; i++) {
                                    field[vertical][horizontal+i] = shipCell;
                                    fieldWithShips[vertical][horizontal+i] = totalPlayerShips;
                                }

                            } else {
                                System.out.println("Здесь ставить корабль нельзя!");
                            }
                            break;
                        case 4:
                            // Проверка на near других кораблей
                            for (int i = -1; i < 2; i++) {
                                for (int j = -1; j < choice1+1; j++) {
                                    try {
                                        if (field[vertical-i][horizontal-j].equals(shipCell)) {
                                            isOtherShipsNotNear = false;
                                        }
                                    } catch (ArrayIndexOutOfBoundsException e) {
                                        continue;
                                    }
                                }
                            }
                            // Проверка на границы
                            try {
                                for (int i = 0; i < choice1; i++) {
                                    field[vertical][horizontal-i].isEmpty();
                                }
                            } catch (ArrayIndexOutOfBoundsException e) {
                                isBorderNotNear = false;
                            }
                            if (isBorderNotNear && isOtherShipsNotNear) {
                                for (int i = 0; i < choice1; i++) {
                                    field[vertical][horizontal-i] = shipCell;
                                    fieldWithShips[vertical][horizontal-i] = totalPlayerShips;
                                }

                            } else {
                                System.out.println("Здесь ставить корабль нельзя!");
                            }
                            break;
                    }
                } else if (choice1 == 1) {
                    // Проверка на near других кораблей
                    for (int i = -1; i < 2; i++) {
                        for (int j = -1; j < choice1+1; j++) {
                            try {
                                if (field[vertical-i][horizontal-j].equals(shipCell)) {
                                    isOtherShipsNotNear = false;
                                }
                            } catch (ArrayIndexOutOfBoundsException e) {
                                continue;
                            }
                        }
                    }
                    if (isOtherShipsNotNear) {
                        field[vertical][horizontal] = shipCell;
                        fieldWithShips[vertical][horizontal] = totalPlayerShips;
                    } else {
                        System.out.println("Здесь ставить корабль нельзя!");
                    }
                }
                if (isBorderNotNear && isOtherShipsNotNear) {
                    switch (choice1) {
                        case 1:
                            oneCellSheep -= 1;
                            break;
                        case 2:
                            twoCellSheep -= 1;
                            break;
                        case 3:
                            threeCellSheep -= 1;
                            break;
                        case 4:
                            fourCellSheep -= 1;
                            break;
                        default:
                            break;
                    }
                    totalPlayerShips-=1;
                }
            }
            show();
            System.out.println("Все корабли игрока-" + id + " расставлены!");
            playerWait();
        }
        private int getHorizontal(char ch) {
            return switch (ch) {
                case 'А' -> 0;
                case 'Б' -> 1;
                case 'В' -> 2;
                case 'Г' -> 3;
                case 'Д' -> 4;
                case 'Е' -> 5;
                case 'Ж' -> 6;
                case 'З' -> 7;
                case 'И' -> 8;
                case 'К' -> 9;
                default -> -1;
            };
        }
    }
}