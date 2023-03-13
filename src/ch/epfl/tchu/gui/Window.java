package ch.epfl.tchu.gui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;

public final class Window {

    public static void display(){
        Stage window = new Stage();
        window.initModality(Modality.APPLICATION_MODAL);
        window.setTitle("règles de tCHu");
        window.getIcons().add(new Image("subway.png"));

        Label title = new Label("But du Jeu");
        title.getStyleClass().add("label-title");
        Text textIntro = new Text("\n" +
                "Le but du jeu est d’obtenir le plus de points. On gagne des points de la manière suivante :\n" +
                "◆ 1. En capturant une route entre 2 villes.\n" +
                "◆ 2. En reliant par une route, en continu, les deux villes d’une carte Destination.\n" +
                "◆ 3. En réalisant le chemin le plus long.\n" +
                "\n" +
                "Les points sont enlevés, et non gagnés, si deux villes d’une carte Destination conservée par \n" +
                "le joueur ne sont pas reliées en continu à la fin du jeu.\n" + "\n");

        Label title1 = new Label("Tour de jeu");
        title1.getStyleClass().add("label-title");

        Text text = new Text("le joueur doit faire une et une seule des 4 actions suivantes : \n");

        Label subtitle1 = new Label("1. Prendre des cartes Wagons ");
        Text text1 = new Text("Le joueur peut prendre 2 cartes Wagon. Il peut prendre n’importe quelle carte visible\n"+
                "parmi les 5 posées ou tirer une carte du dessus de la pioche (tirage en aveugle). Si le joueur prend \n"+
                "une carte visible, il la remplace immédiatement par une autre du dessus de la pioche. Il peut ensuite\n"+
                "prendre une deuxième carte, soit visible, soit dans la pioche.\n");


        Label subtitle2 = new Label("2. Prendre possession d’une route ");

        Text text2 = new Text("Le joueur peut s’emparer d’une route en posant autant de cartes Wagons de la couleur de  \n" +
                "la route que d’espaces composant la route. Après avoir défaussé ses cartes, le joueur pose alors ses \n"+
                "wagons sur chaque espace constituant la route. Enfin, il s'attribut des points en se référant au \n"+
                "tableau de décompte des points.\n");

        Label subtitle3 = new Label("3. Prendre des billets supplementaires ");
        Text text3 = new Text("Le joueur prend 3 billets du dessus de la pioche. Il doit en conserver au moins un, mais  \n"+
                "peut aussi garder 2 ou 3 billets.\n");


        Label subtitle4 = new Label("4. Echanger des routes entre deux joueurs -");
        Text text4 = new Text("Le joueur clique sur une des routes que possèdent son adversaire et clique sur une de ses \n"+
                "propres routes. L'adversaire décide s'il accepte l'échange ou non. Il faut savoir qu'un transfert des\n"+
                "points des routes seront prises en compte. L'adversaire devra donc jouer stratégiquement \n");


        Label title2 = new Label("Cartes Wagon");
        title2.getStyleClass().add("label-title");

        Text text5 = new Text("Il existe 8 types de wagons différents en plus de la locomotive. Les couleurs de chaque  \n"+
                "carte Wagon correspondent aux couleurs présentes sur le plateau du jeu afin de connecter les villes : \n"+
                "Noir, Violet, Bleu, Vert, Jaune, Orange, Rouge et Blanc \n");

        Image wagons = new Image("wagons.png");
        ImageView imageView = new ImageView(wagons);

        Text text5_1 = new Text("Les locomotives sont multicolores et, comme des cartes joker, peuvent remplacer \n"+
                "n’importe quelle couleur lors de la prise de possession d’un tunnel (routes tracées en pointillées.\n"+
                "Il est possible de ne jouer que des cartes Locomotive pour prendre un tunnel.\n"+
                "Un joueur peut avoir en main et à tout moment autant de cartes qu’il le souhaite.\n" +
                "Quand la pioche de cartes est épuisée, les cartes défaussées sont soigneusement mélangées pour reconstituer\n"+
                "une nouvelle pioche. \n");

        Label title3 = new Label("Prendre possession des routes");
        title3.getStyleClass().add("label-title");
        Text text6 = new Text("Pour prendre possession d’une route, un joueur doit jouer une série de cartes égale à la\n"+
                "longueur de la route. La série de cartes doit être composée de cartes du même type. La plupart des \n" +
                "routes nécessitent une série de cartes de couleur spécifique. Par exemple, les routes bleues sont \n"+
                "capturées en posant des cartes Wagon bleues. Certaines routes – en gris sur le plateau - peuvent être \n" +
                "capturées en utilisant n’importe quelle série d’une même couleur.\n" +
                "Lorsqu’une route a été capturée, elle devient de la même couleur que celui du joueur. Toutes les cartes\n"+
                " utilisées pour s’approprier de cette route sont défaussées.\n"+"\n"+
                "Un joueur peut prendre possession de n’importe quelle route sur le plateau de jeu. Il n’est pas obligé \n"+
                "de se connecter avec les routes déjà à son actif.\n" +
                "\n"+
                "Une route prise par un joueur devient sa propriété exclusive. Aucun autre joueur ne peut plus revendiquer \n"+
                "son usage ou son occupation. Certaines villes sont connectées par des routes doubles. Un même joueur ne \n"+
                "peut pas prendre 2 routes reliant les 2 mêmes villes");

        Label title4 = new Label("Prendre possession des tunnels");
        title4.getStyleClass().add("label-title");
        Text text7 = new Text("Pour pouvoir s'emparer d'un tunnel, un joueur doit remplir les mêmes conditions que pour \n"+
                "une route en surface. Dans le cas d'un tunnel, il peut toutefois aussi utiliser les cartes locomotive, \n" +
                "qui sont en quelque sorte multicolores et peuvent donc jouer le rôle d'une carte wagon de couleur quelconque.\n" +
                "Si un joueur désirant s'emparer d'un tunnel dispose des cartes et wagons nécessaires, il doit tout d'abord \n" +
                "placer devant lui les cartes qu'il désire utiliser, après quoi les trois cartes se trouvant au sommet de la \n"+
                "pioche sont retournées. Ces trois cartes déterminent les éventuelles cartes additionnelles que le joueur doit \n"+
                "jouer, en plus de celles qu'il a déjà posées, pour pouvoir s'emparer du tunnel.\n" +
                "\n" +
                "Ainsi, chacune des trois cartes retournées qui est soit une carte wagon de même couleur qu'une carte posée \n"+
                "initialement, soit une carte locomotive, implique l'utilisation d'une carte additionnelle. Ces cartes \n"+
                "additionnelles peuvent être soit des cartes wagon de la même couleur que celles utilisées initialement, \n" +
                "soit des cartes locomotive.\n"+
                "\n" +
                "Si le joueur possède les cartes additionnelles nécessaires, et s'il accepte de les jouer, alors il peut s'emparer \n"+
                "du tunnel. S'il ne possède pas les cartes, ou ne désire pas les jouer, il reprend toutes les cartes en main et\n"+
                "termine son tour.\n" +
                "\n" +
                "Notez qu'il est parfaitement possible de n'utiliser que des cartes locomotives pour tenter de s'emparer  \n"+
                "d'un tunnel. Dans ce cas, seules les éventuelles cartes locomotive figurant parmi les trois cartes  \n"+
                "retournées impliquent l'utilisation de cartes additionnelles, qui doivent alors obligatoirement être\n"+"des locomotives.\n");

        Label title5 = new Label("Prendre possession des tunnels");
        title5.getStyleClass().add("label-title");
        Text text8 = new Text("Lorsqu’un joueur prend possession d’une route, il enregistre ses points en déplaçant son marqueur de score sur le plateau :\n");
        Image points = new Image("points.png");
        ImageView imageView1 = new ImageView(points);

        Label title6 = new Label("Prendre des billets");
        title6.getStyleClass().add("label-title");
        Text text9 = new Text("Un joueur peut utiliser son tour de jeu pour récupérer des billets supplémentaires. Pour\n"+
                "cela, il doit prendre 3 cartes sur le dessus de la pile des tickets. Il doit conserver au moins l’une \n"+
                "des trois cartes, mais peut bien sûr en garder 2 ou même 3. S’il reste moins de 3 billets dans la pile,\n"+
                "le joueur ne peut prendre que le nombre de cartes disponibles. Chaque carte qui n’est pas conservée par \n"+
                "le joueur est remise face cachée sous la pile.\n" +
                "Si le joueur réalise la connexion entre les deux villes d’un billet, il remporte le nombre de points\n"+
                "indiqué sur le billet. Si la connexion n’est pas réalisée, le joueur déduit de son nombre de points déjà\n"+
                "acquis le nombre indiqué sur la carte.\n\n"+

                "Il existe de trois types de billets tCHu:\n"+
                "1. ville à ville,\n"+"2. pays à ville,\n"+"3. pays à pays.\n");

        Label subtitle5 = new Label("1. Billet ville à ville ");
        Text text10 = new Text("Un billet «ville à ville» relie simplement deux villes (suisses) entre elles. Un nombre\n"+
                " de points fixe est attribué à ce type de billet, qui correspond souvent à la longueur du plus court \n"+
                "chemin reliant les deux villes\n" +
                "Un joueur possédant un tel billet et réussissant effectivement à relier les deux villes avant la fin de \n"+
                "la partie gagne les points correspondants. S'il ne parvient pas à relier les villes, il perd ces points,\n"+
                "qui sont soustraits de son total.");

        Label subtitle6 = new Label("2. Billet ville à pays ");
        Text text11 = new Text("Un billet «ville à pays» relie une ville suisse à chacun des pays voisins (France, \n"+
                "Allemagne, Autriche et Italie). Les points attribués à ce type de billet dépendent du pays destination. \n"+
                "Par exemple, dans le cas du billet allant de Berne à un pays voisin, la liaison avec l'Allemagne vaut \n"+
                " 6 points, celle avec l'Autriche 11, celle avec l'Italie 8 et celle avec la France 5. Là aussi, ces \n"+
                "points correspondent généralement à la longueur du plus court chemin reliant la ville à la gare la plus\n"+
                " proche du pays en question.\n\n" +
                "Un joueur possédant un billet ville à pays et réussissant effectivement à relier la ville à au moins \n"+
                "une gare d'un des pays voisins gagne le nombre de points maximum correspondant. S'il ne réussit pas à \n"+
                "relier la ville avec une gare d'un des pays voisins, il perd le nombre minimum de points.\n\n"+
                "Dans l'exemple ci-dessus, un joueur ayant réussi à relier Berne à la France c-à-d à au moins une gare \n"+
                "française, peu importe laquelle—et à l'Italie gagne 8 points, pour la connexion avec l'Italie. Par \n"+
                "contre, s'il ne réussit à relier Berne à aucun des pays voisins, il ne perd que 5 points, le minimum \n"+
                "possible—pour la connexion avec la France.");

        Label subtitle7 = new Label("3. Billet ville à pays ");
        Text text12 = new Text("Un billet «pays à pays» relie un pays voisin de départ donné à chacun des autres pays \n"+
                "voisins, par exemple la France à chacun des pays restants (Allemagne, Autriche et Italie). Là aussi, le\n"+
                "nombre de points dépend du pays. Dans l'exemple du billet partant de la France, la connexion avec l'Allemagne \n"+
                "vaut 5 points, celle avec l'Autriche 14 et celle avec l'Italie 11.\n" +
                "\n" +
                "Les points gagnés ou perdus à la fin de la partie par un joueur possédant un tel billet sont déterminés \n"+
                "de la même manière que pour les billets ville à pays.");

        Label title7 = new Label("Fin du jeu");
        title7.getStyleClass().add("label-title");
        Text text13 = new Text("La fin d'une partie de tCHu est déterminée par le nombre de wagons restant aux joueurs, \n"+
                "de la manière suivante: dès qu'un joueur termine son tour avec deux wagons ou moins, tous les joueurs—lui\n"+
                "compris—jouent encore un tour, après quoi la partie est terminée.");

        Label title8 = new Label("Calcul des points");
        title8.getStyleClass().add("label-title");
        Text text14 = new Text("Chaque joueur doitajouter (ou soustraire) la valeur de chaque billet qu’il a en main. \n"+
                "Si la connexion entre les deux gares est réussie, on ajoute, si elle a échoué, on soustrait.\n\n" +
                "Enfin, le joueur qui a le chemin continu le plus long marque 10 points supplémentaires. En cas d’égalité, \n"+
                "les deux joueurs marquent 10 points. Ne sont pris en compte que les wagons faisant partie du chemin le \n"+
                "plus long en continu (si le chemin bifurque, la bifurcation ne compte pas).\n");

        VBox layout = new VBox(10);
        layout.getChildren().addAll(title, textIntro,
                title1, text,
                subtitle1, text1,
                subtitle2, text2,
                subtitle3, text3,
                subtitle4, text4,
                title2, text5, imageView, text5_1,
                title3, text6,
                title4, text7,
                title5, text8, imageView1,
                title6, text9,
                subtitle5, text10,
                subtitle6, text11,
                subtitle7, text12,
                title7, text13,
                title8, text14);
        layout.setAlignment(Pos.CENTER);

        ScrollPane scroll = new ScrollPane();
        scroll.setContent(layout);
        scroll.setPannable(true);
        scroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scroll.setBackground(new Background(new BackgroundFill(Color.ROYALBLUE, CornerRadii.EMPTY, Insets.EMPTY)));

        Scene scene = new Scene(scroll, 800, 475, Color.ROYALBLUE);
        scene.getStylesheets().add("background.css");
        window.setScene(scene);
        window.showAndWait();

    }

}