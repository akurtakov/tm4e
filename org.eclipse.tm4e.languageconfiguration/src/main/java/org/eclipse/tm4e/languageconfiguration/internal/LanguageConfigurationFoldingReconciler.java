/**
 * Copyright (c) 2025 Red Hat Inc. and others.
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 * Red Hat Inc. - initial API and implementation
 */
package org.eclipse.tm4e.languageconfiguration.internal;

import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.reconciler.Reconciler;
import org.eclipse.jface.text.source.projection.ProjectionViewer;

public class LanguageConfigurationFoldingReconciler extends Reconciler {

	private final LanguageConfigurationFoldingStrategy fStrategy;
	public LanguageConfigurationFoldingReconciler() {
		fStrategy = new LanguageConfigurationFoldingStrategy();
		this.setReconcilingStrategy(fStrategy, IDocument.DEFAULT_CONTENT_TYPE);
	}

	@Override
	public void install(ITextViewer textViewer) {
		super.install(textViewer);
		ProjectionViewer pViewer =(ProjectionViewer)textViewer;
		fStrategy.setProjectionViewer(pViewer);
	}


}
