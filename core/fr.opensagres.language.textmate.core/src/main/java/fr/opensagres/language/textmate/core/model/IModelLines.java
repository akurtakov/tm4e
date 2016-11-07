/**
 *  Copyright (c) 2015-2016 Angelo ZERR.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 *
 *  Contributors:
 *  Angelo Zerr <angelo.zerr@gmail.com> - initial API and implementation
 */
package fr.opensagres.language.textmate.core.model;

/**
 * Mode lines API which must be initalize with a document and changed of
 * document.
 *
 */
public interface IModelLines {

	/**
	 * Add a new line at specified index line.
	 * 
	 * @param line
	 */
	void addLine(int line);

	/**
	 * Remove the line at specified index line.
	 * 
	 * @param line
	 */
	void removeLine(int line);

	/**
	 * Mark as line is updated.
	 * 
	 * @param line
	 */
	void updateLine(int line);

	/**
	 * Return lines size.
	 * 
	 * @return lines size.
	 */
	int getSize();

}
