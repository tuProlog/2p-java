/*
 * tuProlog - Copyright (C) 2001-2003  aliCE team at deis.unibo.it
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */
package alice.tuprologx.ide;

import alice.tuprolog.*;

import java.util.*;

/**
 * A dynamic manager for tuProlog libraries.
 *
 * @author	<a href="mailto:giulio.piancastelli@studio.unibo.it">Giulio Piancastelli</a>
 * @version	1.0 - 14-feb-03
 */

public final class LibraryManager {

    /** The Prolog engine referenced by the Library Manager. */
    private Prolog engine;
    /** The LibraryManager class is a Singleton. */
    private static final LibraryManager singleton = new LibraryManager();
    /** Stores classnames for managed libraries. */
    private static Vector libraries;

    static {
        libraries = new Vector();
        /* Initialize the repository for managed libraries using the
         * standard libraries which come with the tuProlog core package. */
        libraries.addElement("alice.tuprolog.lib.JavaLibrary");
        libraries.addElement("alice.tuprolog.lib.ISOLibrary");
        libraries.addElement("alice.tuprolog.lib.IOLibrary");
        libraries.addElement("alice.tuprolog.lib.BasicLibrary");
    }

    /** Enforcing the sinlgeton pattern with a private no-args constructor. */
    private LibraryManager() {}

    /**
     * Get the LibraryManager.
     *
     * @return the singleton instance of LibraryManager.
     */
    public static LibraryManager getInstance() {
        return singleton;
    }

    /**
     * Set the engine to be referenced by the library manager.
     *
     * @param engine The engine to be referenced by the library manager.
     */
    public void setEngine(Prolog engine) {
        this.engine = engine;
    }

    /**
     * Get the engine referenced by the library manager.
     *
     * @return the engine referenced by the library manager.
     */
    public Prolog getEngine() {
        return engine;
    }

    /**
     * Check if a library is loaded into the Prolog engine.
     *
     * @param libraryClassname The complete name of the library class to check.
     * @return true if the library is loaded into the engine, false otherwise.
     */
    public boolean isLibraryLoaded(String libraryClassname) {
        return (engine.getLibrary(libraryClassname) != null);
    }

    /**
     * Add a library to the manager.
     *
     * @param libraryClassname The name of the .class of the library to be added.
     * @throws ClassNotFoundException if the library class cannot be found.
     * @throws InvalidLibraryException if the library is not a valid tuProlog library.
     */
    public void addLibrary(String libraryClassname) throws ClassNotFoundException, InvalidLibraryException {
        Class library = Class.forName(libraryClassname);
        if (library.getSuperclass().equals(alice.tuprolog.Library.class))
            libraries.addElement(libraryClassname);
        else
            throw new InvalidLibraryException(libraryClassname);
    }

    /**
     * Get the libraries managed by the library manager.
     *
     * @return The libraries managed by the library manager as an array of
     * <code>Object</code>s.
     */
    public Object[] getLibraries() {
        return libraries.toArray();
    }

    /**
     * Load a library from the Library Manager into the engine.
     *
     * @param library The library to be loaded into the engine.
     * @throws InvalidLibraryException if the library is not a valid one.
     */
    public void loadLibrary(String library) throws InvalidLibraryException {
        engine.loadLibrary(library);
    }

    /**
     * Unload a library from the Library Manager out of the engine.
     *
     * @param library The library to be unloaded out of the engine.
     * @throws InvalidLibraryException if the library is not a valid one.
     */
    public void unloadLibrary(String library) throws InvalidLibraryException {
        engine.unloadLibrary(library);
    }

} // end LibraryManager class