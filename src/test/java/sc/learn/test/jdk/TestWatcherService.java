package sc.learn.test.jdk;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;

import org.junit.Test;

public class TestWatcherService {
	@Test
	public void testFileSystemWatch() throws InterruptedException, IOException  {
		Path path=Paths.get("/home", "shenchao3");
		WatchService watcher = FileSystems.getDefault().newWatchService();
		path.register(watcher, StandardWatchEventKinds.ENTRY_CREATE, StandardWatchEventKinds.ENTRY_DELETE, StandardWatchEventKinds.ENTRY_MODIFY);
		while (true) {
			WatchKey key = watcher.take();
			for (WatchEvent<?> event : key.pollEvents()) {
				WatchEvent.Kind<?> kind = event.kind();
				if (kind == StandardWatchEventKinds.OVERFLOW) {// 事件可能lost or // discarded
					continue;
				}
				Path fileName = (Path) event.context();
				System.out.printf("Event %s has happened,which fileName is %s%n", kind.name(), fileName);
			}
			if (!key.reset()) {
				break;
			}
		}
	}
}