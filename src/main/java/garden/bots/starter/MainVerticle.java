package garden.bots.starter;

import java.io.IOException;
import java.nio.file.*;

import org.wasmer.Instance;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;


public class MainVerticle extends AbstractVerticle {

  @Override
  public void start(Promise<Void> startPromise) throws Exception {
    vertx.createHttpServer().requestHandler(req -> {
      req.response()
        .putHeader("content-type", "text/plain")
        .end("Hello from Vert.x!");
    }).listen(8888, http -> {
      if (http.succeeded()) {
        startPromise.complete();

        // `simple.wasm` is located at `tests/resources/`.
        Path wasmPath = Paths.get(new MainVerticle().getClass().getClassLoader().getResource("hey_rust.wasm").getPath());

        // Reads the WebAssembly module as bytes.
        byte[] wasmBytes;
        try {
          wasmBytes = Files.readAllBytes(wasmPath);

          // Instantiates the WebAssembly module.
          Instance instance = new Instance(wasmBytes);

          // Calls an exported function, and returns an object array.
          Object[] results = instance.exports.getFunction("sum").apply(5, 37);

          System.out.println((Integer) results[0]); // 42

          // Drops an instance object pointer which is stored in Rust.
          instance.close();



        } catch (IOException e) {
          // TODO Auto-generated catch block
          e.printStackTrace();
        }



        System.out.println("HTTP server started on port 8888");
      } else {
        startPromise.fail(http.cause());
      }
    });
  }
}
