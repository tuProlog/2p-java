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

import javax.swing.JApplet;

/**
 * A class for serving tuProlog as an applet.
 * Note that since tuProlog cannot run under JVM 1.1, the launched IDE is directly
 * the one for the Java2 platform, without passing through GUILauncher.
 * 
 * @author	<a href="mailto:giulio.piancastelli@studio.unibo.it">Giulio Piancastelli</a>
 * @version	1.0 - 7-apr-03
 */

public class Applet extends JApplet {

    public void init() {
        new JavaIDE().show();
    }

} // end Applet class