/*
 * Created on Nov 5, 2003
 * 
 * Copyright (C)aliCE team at deis.unibo.it
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
 *
 */
package alice.test.tuprolog;

import alice.tuprolog.*;
import java.util.*;

public class HashLibrary extends Library {
	
	private HashMap dicts = new HashMap();
	private int counter = 0;
		
	public boolean new_dict_1(Var arg0){
		HashMap map = new HashMap();
		String dict_name = "dict_"+counter++;
		dicts.put(dict_name,map);
		return unify(arg0, new Struct(dict_name));
	} 
	
	public boolean dict_put_3(Struct dictRef, Struct key, Term object){
		HashMap dict = (HashMap)dicts.get(dictRef.toString());
		dict.put(key.toString(),object);
		return true;
	}
	
	public boolean dict_get_3(Struct dictRef, Struct key, Term res){
		HashMap dict = (HashMap)dicts.get(dictRef.toString());
		Term result = (Term)dict.get(key.toString());
		return unify(res,result);
	}
	
	public boolean dict_remove_2(Struct dictRef, Struct key){
		HashMap dict = (HashMap)dicts.get(dictRef.toString());
		dict.remove(key.toString());
		return true;
	}
}
