package nz.tomasborsje.duskfall.database;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.ServerApi;
import com.mongodb.ServerApiVersion;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.ReplaceOptions;
import nz.tomasborsje.duskfall.DuskfallServer;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static com.mongodb.client.model.Filters.eq;

public class DatabaseConnection {
    private final ServerApi serverApi = ServerApi.builder().version(ServerApiVersion.V1).build();
    private final MongoClient client;
    private final MongoDatabase mmoDatabase;

    private final ReplaceOptions UPSERT = new ReplaceOptions().upsert(true);

    public DatabaseConnection(String connectionString) {
        MongoClientSettings clientSettings = MongoClientSettings.builder()
                .applyConnectionString(new ConnectionString(connectionString))
                .serverApi(serverApi)
                .build();

        client = MongoClients.create(clientSettings);
        mmoDatabase = client.getDatabase("mmo");
    }

    public @NotNull PlayerData getPlayerData(String username) {
        Document playerDoc = getPlayerDocument(username);

        DuskfallServer.logger.info("Getting player data for username {}!", username);

        if(playerDoc == null) {
            return new PlayerData(username, 1);
        }

        // Read fields
        int level = playerDoc.getInteger("level");

        return new PlayerData(username, level);
    }

    public void savePlayerData(PlayerData playerData) {
        Document doc = new Document();

        doc.put("username", playerData.username);
        doc.put("level", playerData.level);

        Bson query = eq("username", playerData.username);

        // Save document to database
        getPlayers().replaceOne(query, doc, UPSERT);
    }

    private @Nullable Document getPlayerDocument(String username) {
        return getPlayers().find(eq("username", username)).first();
    }

    private MongoCollection<Document> getPlayers() {
        return mmoDatabase.getCollection("players");
    }

    public void disconnect() {
        client.close();
    }
}
