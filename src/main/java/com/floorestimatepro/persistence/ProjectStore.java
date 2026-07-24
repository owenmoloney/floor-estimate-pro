package com.floorestimatepro.persistence;
import java.util.List;
import java.util.ArrayList;
import com.floorestimatepro.model.Project;
import com.floorestimatepro.model.Room;
import com.floorestimatepro.model.PlanPoint;
import com.floorestimatepro.model.Obstacle;
import com.floorestimatepro.model.Calibration;
import com.google.gson.Gson;
import java.nio.file.Files;
import java.nio.file.Path;

record PointData(double x, double y){
    
}

record CalibrationData(double feetPerPixel){
}

record RoomData(List<PointData> listOfPoints){

}

record ObstacleData(List<PointData> listOfPoints){

}

record ProjectData(
    String imagePath,
    CalibrationData calibration,
    List<RoomData> rooms,
    List<ObstacleData> obstacles
){};


public class ProjectStore{
    public static ProjectData toData(Project project){

        CalibrationData calData = null;

        if(project.calibration() != null){
                calData = new CalibrationData(
                project.calibration().feetPerPixel()
            );
        }

        List<RoomData> roomDatas = new ArrayList<>();

        for(Room room: project.rooms()){
            List<PointData> pointDatas = new ArrayList<>();
                for(PlanPoint point: room.listOfPoints()){
                    pointDatas.add(new PointData(point.x(), point.y()));
                }
            roomDatas.add(new RoomData(pointDatas));
        }

       List<ObstacleData> obstacleDatas = new ArrayList<>();

        for(Obstacle obstacle: project.obstacles()){
            List<PointData> pointDatas = new ArrayList<>();
                for(PlanPoint point: obstacle.listOfPoints()){
                    pointDatas.add(new PointData(point.x(), point.y()));
                }
            obstacleDatas.add(new ObstacleData(pointDatas));
        }

        return new ProjectData(project.imagePath(), calData, roomDatas, obstacleDatas);
    }

    public static Project fromData(ProjectData data){
        Project project =  new Project();

        project.setImagePath(data.imagePath());

        if(data.calibration() != null){
            Calibration cal = new Calibration(data.calibration().feetPerPixel());
            project.setCalibration(cal);
        }
    
        for(RoomData roomData: data.rooms()){
            List<PlanPoint> points = new ArrayList<>();
            for(PointData pd : roomData.listOfPoints()){
                points.add(new PlanPoint(pd.x(), pd.y()));
            }
            project.addRoom(new Room(points));
        }

       for(ObstacleData obstacleData: data.obstacles()){
            List<PlanPoint> points = new ArrayList<>();
            for(PointData po : obstacleData.listOfPoints()){
                points.add(new PlanPoint(po.x(), po.y()));
            }
            project.addObstacle(new Obstacle(points));
        }

        return project;
    }

    public static void save(Project project, String filePath) throws Exception {
        ProjectData data = toData(project);

        String jsonText = new Gson().toJson(data);

        Files.writeString(Path.of(filePath), jsonText);
    }

    public static Project load(String filePath) throws Exception {
        String jsonText = Files.readString(Path.of(filePath));

        ProjectData data = new Gson().fromJson(jsonText, ProjectData.class);

        return fromData(data);
    }
}
