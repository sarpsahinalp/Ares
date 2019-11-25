package de.tum.in.test.api;

import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.util.Objects;

public enum PathType {
	/**
	 * Absolute path must match exactly; given path may be specified relative
	 */
	EXACT() {
		@Override
		public PathMatcher convertToPathMatcher(String s) {
			return Path.of(s).toAbsolutePath().normalize()::equals;
		}
	},
	/**
	 * The path must start with the given path; given path may be specified relative
	 */
	STARTS_WITH {
		@Override
		public PathMatcher convertToPathMatcher(String s) {
			Path begin = Path.of(s).toAbsolutePath().normalize();
			return p -> p.normalize().startsWith(begin);
		}
	},
	/**
	 * applies relative GLOB for a relativized path
	 *
	 * @see FileSystem#getPathMatcher(String)
	 */
	GLOB {
		@Override
		public PathMatcher convertToPathMatcher(String s) {
			PathMatcher pm = DEFAULT_FS.getPathMatcher("glob:" + s);
			return p -> pm.matches(relativizeSafe(p));
		}
	},
	/**
	 * applies relative RegEx for a relativized path
	 *
	 * @see FileSystem#getPathMatcher(String)
	 */
	REGEX {
		@Override
		public PathMatcher convertToPathMatcher(String s) {
			PathMatcher pm = DEFAULT_FS.getPathMatcher("regex:" + s);
			return p -> pm.matches(relativizeSafe(p));
		}
	},
	/**
	 * applies absolute GLOB for an absolute path
	 *
	 * @see FileSystem#getPathMatcher(String)
	 */
	GLOB_ABSOLUTE {
		@Override
		public PathMatcher convertToPathMatcher(String s) {
			PathMatcher pm = DEFAULT_FS.getPathMatcher("glob:" + s);
			return p -> pm.matches(p.normalize());
		}
	},
	/**
	 * applies absolute RegEx for an absolute path
	 *
	 * @see FileSystem#getPathMatcher(String)
	 */
	REGEX_ABSOLUTE {
		@Override
		public PathMatcher convertToPathMatcher(String s) {
			PathMatcher pm = DEFAULT_FS.getPathMatcher("regex:" + s);
			return p -> pm.matches(p.normalize());
		}
	};

	/**
	 * @param s the format/pattern
	 * @return a {@link PathMatcher} that is configured to match absolute paths.
	 * @author Christian Femers
	 */
	public abstract PathMatcher convertToPathMatcher(String s);

	static final Path CURRENT_PATH = Path.of("").toAbsolutePath();
	static final FileSystem DEFAULT_FS = FileSystems.getDefault();

	private static Path relativizeSafe(Path any) {
		Path p = any.normalize().toAbsolutePath();
		if (!Objects.equals(p.getRoot(), CURRENT_PATH.getRoot()))
			return p;
		return CURRENT_PATH.relativize(p).normalize();
	}
}
