package io.github.ritonglue.goxlsx.processor;

public interface CallbackIndex {
	void missingIndex(AnnotationStorer storer);
	void outOfRangeIndex(AnnotationStorer storer);
}
