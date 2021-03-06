/*
 -----------------------------------------------------------------------------------
 Cours       : Génie logiciel (GEN)
 Fichier     : Serve
 Auteur(s)   : Forestier Quentin & Melvyn Herzig
 Date        : 06.03.2021
 -----------------------------------------------------------------------------------
 */

package gen.command;

import gen.FileManager;
import gen.Updatable;
import picocli.CommandLine;
import picocli.CommandLine.Command;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.Callable;

/**
 * Class implémentant la sous commande serve.
 *
 * @author Herzig Melvyn
 */
@Command(name = "serve", description = "Open the site in a web browser.")
public class Serve implements Callable<Integer>, Updatable
{
    @CommandLine.Parameters(index = "0", description = "path to site " +
            "repoistory (the one from init)",
            defaultValue = "/")
    String path;

    @CommandLine.Option(names = {"--watch"}, description = "Relaunch a new " +
            "tab when a modification is detected")
    boolean watch = false;

    private boolean newUpdate = true;


    /**
     * Méthode pour l'appel de la commande serve.
     */
    @Override
    public Integer call()
    {
        // Récupération du chemin travaillable.
        String realPath = FileManager.getRealPath(path);

        openIndexInBrower();

        if (watch)
        {
            try
            {
                FileManager.watch(new File(realPath + "/build").toPath(), this);
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }

        return 0;
    }

    private void openIndexInBrower()
    {

        // Récupération du chemin travaillable.
        String realPath = FileManager.getRealPath(path);

        // Vérifie si le fichier build/index.html est présent.
        File index = new File(realPath + "/build/index.html");

        // Tentative d'ouverture du fichier inddex.html
        try
        {
            if (!index.exists()) // Si le fichier n'existe pas
            {
                throw new RuntimeException("File " + realPath + "/build/index" +
                        ".html unreachable.");
            }
            else // Si le fichier existe
            {
                String uriReady = index.toURI().toString();
                uriReady = uriReady.replace("./", "");
                Desktop.getDesktop().browse(new URI(uriReady));
            }
        }
        catch (Exception e)
        {
            System.out.println(e.getMessage());
        }
    }

    @Override
    public void update(File fileUpdated)
    {

        if (newUpdate)
        {
            openIndexInBrower();
            newUpdate = false;
        }


    }

    @Override
    public void beginNewUpdate()
    {
        newUpdate = true;
    }
}