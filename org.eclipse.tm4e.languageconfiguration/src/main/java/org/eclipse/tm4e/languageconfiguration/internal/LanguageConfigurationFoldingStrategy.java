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

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.content.IContentType;
import org.eclipse.jdt.annotation.Nullable;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.Position;
import org.eclipse.jface.text.reconciler.DirtyRegion;
import org.eclipse.jface.text.reconciler.IReconcilingStrategy;
import org.eclipse.jface.text.reconciler.IReconcilingStrategyExtension;
import org.eclipse.jface.text.source.Annotation;
import org.eclipse.jface.text.source.projection.ProjectionAnnotation;
import org.eclipse.jface.text.source.projection.ProjectionViewer;
import org.eclipse.tm4e.languageconfiguration.internal.model.FoldingRules;
import org.eclipse.tm4e.languageconfiguration.internal.model.RegExPattern;
import org.eclipse.tm4e.languageconfiguration.internal.registry.LanguageConfigurationRegistryManager;
import org.eclipse.tm4e.ui.internal.utils.ContentTypeHelper;

public class LanguageConfigurationFoldingStrategy implements IReconcilingStrategy, IReconcilingStrategyExtension {
	
	private @Nullable IDocument document;
	private @Nullable String oldDocument;
	private @Nullable ProjectionViewer projectionViewer;
	private static final IContentType[] EMPTY_CONTENT_TYPES = new IContentType[0];

	private IContentType[] contentTypes = EMPTY_CONTENT_TYPES;
	
	@Override
	public void setDocument(@Nullable IDocument document) {
		this.document = document;
	}

	public void setProjectionViewer(@Nullable ProjectionViewer projectionViewer) {
		this.projectionViewer = projectionViewer;
	}

	@Override
	public void reconcile(DirtyRegion dirtyRegion, IRegion subRegion) {
		initialReconcile();
	}

	@Override
	public void reconcile(IRegion partition) {
		initialReconcile();
	}

	@Override
	public void initialReconcile() {
		var doc = this.document;
		if (doc == null) return;
		if (doc.get().equals(oldDocument)) return;
		var projViewer = this.projectionViewer;
		if (projViewer == null) return;
		final var contentTypeInfo = ContentTypeHelper.findContentTypes(doc);
		this.contentTypes = contentTypeInfo == null ? EMPTY_CONTENT_TYPES : contentTypeInfo.getContentTypes();
		if (contentTypes.length == 0)
			return;
		
		oldDocument = doc.get();
		
		final var registry = LanguageConfigurationRegistryManager.getInstance();
		
		List<Position> allPositions = new ArrayList<>();
		for (final IContentType contentType : contentTypes) {
			final FoldingRules foldingRules = registry.getFoldingRules(contentType);
			if (foldingRules == null)
				continue;
			List<Position> positions = getNewPositionsOfAnnotations(doc, foldingRules.markers.start, foldingRules.markers.start);
			allPositions.addAll(positions);
		}

		var annotModel = projViewer.getProjectionAnnotationModel();
		if (annotModel == null) return;
		for (Position position : allPositions) {
			Annotation annotation = new ProjectionAnnotation();
			annotModel.addAnnotation(annotation, position);
		}
	}


	private List<Position> getNewPositionsOfAnnotations(IDocument document, RegExPattern start, RegExPattern end){
		List<Position> positions = new ArrayList<>();
		int startIndex = -1;
		for (var i = 0; i < document.getNumberOfLines();i++) {
			try {
				String line = document.get(document.getLineOffset(i), document.getLineLength(i));
				if (startIndex == -1) {
					if (start.matchesPartially(line)) {
						startIndex = document.getLineOffset(i);
					}
					continue;
				}
				if (startIndex != -1 && end.matchesPartially(line)) {
					positions.add(new Position(startIndex, document.getLineOffset(i)+document.getLineLength(i)));
					startIndex = -1;
				}
			} catch (BadLocationException e) {
				continue;
			}
		}
		return positions;
	}

	@Override
	public void setProgressMonitor(@Nullable IProgressMonitor monitor) {
		// no progress monitor used
	}
}
